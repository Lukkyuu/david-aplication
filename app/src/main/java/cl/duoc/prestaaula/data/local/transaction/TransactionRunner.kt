package cl.duoc.prestaaula.data.local.transaction

import androidx.room.RoomDatabase
import androidx.room.withTransaction

interface TransactionRunner {
    suspend fun <T> runInTransaction(block: suspend () -> T): T
}

class RoomTransactionRunner(private val db: RoomDatabase) : TransactionRunner {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return db.withTransaction {
            block()
        }
    }
}

class DirectTransactionRunner : TransactionRunner {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return block()
    }
}
