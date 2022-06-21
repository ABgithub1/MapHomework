package com.example.maphomework.koin

import com.example.maphomework.ui.map.LocationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::LocationService)
}