package main.controller

import com.google.inject.Inject
import main.dao.BusinessDao
import main.dao.Question

class BusinessController @Inject constructor(private val dao: BusinessDao) {

    suspend fun getQuestion(qid: Int): Question {
        return dao.getSingleQuetion(qid)!!
    }
}