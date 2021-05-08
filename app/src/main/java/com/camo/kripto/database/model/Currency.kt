package com.camo.kripto.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
class Currency(@PrimaryKey val id: String)