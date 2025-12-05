package di

import api.OllamaApi
import data.Database
import org.koin.dsl.module
import viewmodels.ChatViewModel
import viewmodels.MainViewModel

val appModule = module {
    single { Database() }
    single { MainViewModel(get()) }
    single { ChatViewModel(get()) }
    single { OllamaApi() }
}