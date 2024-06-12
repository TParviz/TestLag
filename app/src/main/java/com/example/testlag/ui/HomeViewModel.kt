package com.example.testlag.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testlag.domain.model.Points
import com.example.testlag.domain.useCase.GetPointsUseCase
import com.example.testlag.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPointsUseCase: GetPointsUseCase,
    private val application: Application
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _pointList = MutableLiveData<List<Points>>()
    val pointList: LiveData<List<Points>> = _pointList

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun getPoints(count: Int) {
        viewModelScope.launch {
            getPointsUseCase(count).onStart {
                _isLoading.value = true
            }.onCompletion {
                _isLoading.value = false
            }.collect { result ->
                result.onFailure {
                    showToast(it.message ?: "", application)
                    _error.value = true
                }.onSuccess { response ->
                    _error.value = false
                    _pointList.value = response.points.sortedBy { it.x }
                }
            }
        }
    }
}