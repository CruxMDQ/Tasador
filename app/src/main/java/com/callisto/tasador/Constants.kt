package com.callisto.tasador

const val UNINITIALIZED_ID = -1

const val TYPE_HOUSE = "House"
const val TYPE_PARCEL = "Parcel"

const val VIEW_TYPE_PARCEL = 1
const val VIEW_TYPE_HOUSE = 2

// *** DATABASE DEFINES START ***

// Tables
const val TABLE_RE = "RealEstate"
const val TABLE_CHAMBERS = "chambers"

const val RE = "re"
const val SUFFIX_ID = "id"

// Fields
const val COL_RE_ID = RE + "_" + SUFFIX_ID
const val COL_CHAMBER_ID = "chamber_id"
const val COL_PARENT_ID = "parent_id"

const val COL_TYPE = "type"
const val COL_ADDRESS = "address"
const val COL_OWNER = "owner"
const val COL_PRICE_FINAL = "price_final"
const val COL_PRICE_QUOTED = "price_quoted"
const val COL_CHAMBER_FRONT = "front"
const val COL_CHAMBER_SIDE = "side"
const val COL_PARCEL_FRONT = "front"
const val COL_PARCEL_SIDE = "side"
const val COL_AREA = "area"
const val COL_CATASTER = "cataster"
const val COL_ZONIFICATION = "zonification"

const val COL_TAX_NUMBER = "tax_number"         // Partida inmobiliaria

const val COL_UTILITY_WATER = "utility_water"
const val COL_UTILITY_POWER = "utility_power"
const val COL_UTILITY_SEWERS = "utility_sewers"
const val COL_UTILITY_NATGAS = "utility_natgas"
const val COL_UTILITY_DRAINS = "utility_drains"
const val COL_UTILITY_INET = "utility_internet"

const val COL_ROAD_TYPE = "road_type"           // Calzada (pavimento, mejorado, tierra)
// *** DATABASE DEFINES END ***

const val CODE_EXTERNAL_STORAGE: Int = 100
const val CODE_CONTACTS: Int = 101