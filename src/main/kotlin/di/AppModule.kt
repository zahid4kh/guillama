package di

import data.Database
import org.koin.dsl.module
import viewmodels.MainViewModel

val appModule = module {
    single { Database() }
    single { MainViewModel(get()) }
}