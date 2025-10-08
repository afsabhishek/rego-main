package com.rego.screens.base

sealed class NetworkState{

   data object Good: NetworkState()

   data object Failed: NetworkState()

}
