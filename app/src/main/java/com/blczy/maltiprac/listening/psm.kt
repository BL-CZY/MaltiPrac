package com.blczy.maltiprac.listening

import com.blczy.maltiprac.R

val indicesMap = mapOf("shopping" to listOf(PsmDescriptor(R.string.psm_0_description, 0)))

class PsmDescriptor(val descriptionStringIndex: Int, val index: Int) {}

class Psm(val index: Int) {}