package com.nvvi9.ytaudio.di.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class ViewModelFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        (providers[modelClass] ?: providers.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value)?.get() as? T ?: throw IllegalArgumentException("Unknown model class $modelClass")
}