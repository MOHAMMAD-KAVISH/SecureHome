package com.example.securehomeplus.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.securehomeplus.data.database.entities.SecurityFactor
import com.example.securehomeplus.data.repository.SecurityRepository
import com.example.securehomeplus.utils.SecurityEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class EvaluationResult(
    val scorePercent: Int,
    val recommendations: List<String>,
    val failedQuestionIds: List<Int>
)

class SecurityViewModel(private val repository: SecurityRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<SecurityFactor>>()
    val questions: LiveData<List<SecurityFactor>> = _questions

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _evaluation = MutableLiveData<EvaluationResult?>()
    val evaluation: LiveData<EvaluationResult?> = _evaluation

    fun loadQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            val list = repository.getQuestions()
            _questions.postValue(list)
            _loading.postValue(false)
        }
    }

    /**
     * answers: Map<questionId, Boolean> -> true means safe/checked
     */
    fun evaluateAnswers(questions: List<SecurityFactor>, answers: Map<Int, Boolean>) {
        viewModelScope.launch(Dispatchers.Default) {
            _loading.postValue(true)
            val res = SecurityEvaluator.calculateScore(questions, answers)
            _evaluation.postValue(res)
            _loading.postValue(false)
        }
    }
}

class SecurityViewModelFactory(private val repository: SecurityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecurityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SecurityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
