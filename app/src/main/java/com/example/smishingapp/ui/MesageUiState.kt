package com.example.smishingapp.ui

import java.util.SortedSet


data class MessageUiState(
    val allMessage: SortedSet<Message> = sortedSetOf(dateComparator),
    val spam: SortedSet<Message> = sortedSetOf(dateComparator),
    val ham: SortedSet<Message> = sortedSetOf(dateComparator),
    val chat: MutableList<Message> = mutableListOf()
    )