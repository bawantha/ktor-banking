package com.example.routes

import com.example.models.Partner
import com.example.models.Invoice
import com.example.models.LedgerItem
import com.example.models.LedgerJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.models.Database
import com.example.models.Transaction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.text.SimpleDateFormat
import org.litote.kmongo.eq
import org.litote.kmongo.or
import java.math.BigDecimal

fun Route.ledgerRoutes(){
    val invoicesCollection = Database.db.getCollection<Invoice>("invoices")
    val transactionsCollection = Database.db.getCollection<Transaction>("transactions")
    val partnersCollection = Database.db.getCollection<Partner>("partners")

    route("/ledger"){
        // Route to get ledger items
        get {
            val partnerId = call.request.queryParameters["partnerId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing Partner ID")
            val startDateString = call.request.queryParameters["startDate"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing Start Date")
            val endDateString = call.request.queryParameters["endDate"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing End Date")

            // Define a date format pattern
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

            // Parse the date strings into Date objects
            val startDate = dateFormat.parse(startDateString)
            val endDate = dateFormat.parse(endDateString)

            try {
                val partner = partnersCollection.findOneById(partnerId)

                var runningBalance = BigDecimal(partner?.openingBalance ?: "0")

                // Fetch all invoices
                val allInvoices = invoicesCollection.find(Invoice::partnerId eq partnerId).toList()

                // Fetch all transactions
                val allTransactions = transactionsCollection.find(or(Transaction::paymentTo eq partnerId, Transaction::receiptFrom eq partnerId)).toList()

                // Calculate balance from prior entries (before startDate)
                allInvoices.forEach { invoice ->
                    val invoiceDate = dateFormat.parse(invoice.invoiceDate)
                    if (invoiceDate.before(startDate)) {
                        val amount = try { BigDecimal(invoice.invoiceTotal) } catch (e: Exception) { BigDecimal.ZERO }
                        if (invoice.type == "sales") {
                            runningBalance = runningBalance.add(amount) // Debit
                        } else if (invoice.type == "purchase") {
                            runningBalance = runningBalance.subtract(amount) // Credit
                        }
                    }
                }

                allTransactions.forEach { transaction ->
                    val txDate = dateFormat.parse(transaction.date)
                    if (txDate.before(startDate)) {
                        val amount = try { BigDecimal(transaction.amount) } catch (e: Exception) { BigDecimal.ZERO }
                        when (transaction.type) {
                            "BRV", "CRV" -> runningBalance = runningBalance.subtract(amount) // Credit (Receipt)
                            "BPV", "CPV" -> runningBalance = runningBalance.add(amount) // Debit (Payment)
                        }
                    }
                }

                // Filter the invoices based on the date range
                val filteredInvoices = allInvoices.filter { invoice ->
                    val invoiceDate = dateFormat.parse(invoice.invoiceDate)
                    invoiceDate in startDate..endDate
                }

                // Map all invoices to ledger items
                val invoices = filteredInvoices.map { invoice ->
                    val amount = try { BigDecimal(invoice.invoiceTotal) } catch (e: Exception) { BigDecimal.ZERO }
                    val debit = if (invoice.type == "sales") amount else BigDecimal.ZERO
                    val credit = if (invoice.type == "purchase") amount else BigDecimal.ZERO
                    LedgerItem(
                        date = invoice.invoiceDate,
                        instrumentNo = invoice.invoiceNo,
                        reference = invoice.reference,
                        description = if (invoice.type == "sales") "Sales" else "Purchase",
                        quantity = invoice.invoiceItems.count().toString(),
                        debit = debit.toString(),
                        credit = credit.toString(),
                        balance = "0" // to be updated below
                    )
                }

                // Filter the transactions based on the date range
                val filteredTransactions = allTransactions.filter { transaction ->
                    val date = dateFormat.parse(transaction.date)
                    date in startDate..endDate
                }

                // Map all transactions to ledger items
                val transactions = filteredTransactions.map { transaction ->
                    val amount = try { BigDecimal(transaction.amount) } catch (e: Exception) { BigDecimal.ZERO }
                    val (description, debit, credit) = when (transaction.type) {
                        "BRV", "CRV" -> Triple("Receipt", BigDecimal.ZERO, amount)
                        "BPV", "CPV" -> Triple("Payment", amount, BigDecimal.ZERO)
                        else -> Triple("", BigDecimal.ZERO, BigDecimal.ZERO)
                    }

                    LedgerItem(
                        date = transaction.date,
                        instrumentNo = transaction.voucherNo,
                        reference = transaction.reference,
                        description = description,
                        quantity = "0",
                        debit = debit.toString(),
                        credit = credit.toString(),
                        balance = "0", // to be updated below
                    )
                }

                // Combine the transformed records from both collections into a single list
                val sortedItems = (invoices + transactions).sortedBy { LocalDate.parse(it.date) }

                val ledgerItemsList = mutableListOf<LedgerItem>()

                // Add Opening Balance entry
                ledgerItemsList.add(
                    LedgerItem(
                        date = startDateString,
                        instrumentNo = "",
                        reference = "",
                        description = "Opening Balance",
                        quantity = "0",
                        debit = "0",
                        credit = "0",
                        balance = runningBalance.toString()
                    )
                )

                // Update running balance and construct the final list
                for (item in sortedItems) {
                    val debitAmt = try { BigDecimal(item.debit) } catch (e: Exception) { BigDecimal.ZERO }
                    val creditAmt = try { BigDecimal(item.credit) } catch (e: Exception) { BigDecimal.ZERO }
                    runningBalance = runningBalance.add(debitAmt).subtract(creditAmt)
                    ledgerItemsList.add(item.copy(balance = runningBalance.toString()))
                }

                // Respond with the list of invoices with their corresponding partners
                val jsonResponse = Json.encodeToString(LedgerJson(partner, ledgerItemsList))
                call.respond(HttpStatusCode.OK, jsonResponse)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve invoices.")
            }
        }
    }
}