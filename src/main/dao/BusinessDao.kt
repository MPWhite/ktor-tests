package main.dao

import com.google.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }

data class Question(val id: Int , val question: String)

object Questions: IntIdTable() {
    val question = text("question")
}

data class Answer(val id: Int, val answer: String, val questionId: Int)

object Answers: IntIdTable() {
    val answer = text("answer")
    val question_id = entityId("question_id", Questions)
}

class BusinessDao {
    private fun toQuestion(row: ResultRow): Question =
        Question(id = row[Questions.id].value, question = row[Questions.question])

    suspend fun getSingleQuetion(id: Int): Question? = dbQuery {
        Questions.select {
            Questions.id eq id
        }.mapNotNull { toQuestion(it) }.singleOrNull()
    }
}