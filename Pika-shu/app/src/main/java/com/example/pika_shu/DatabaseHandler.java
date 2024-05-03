package com.example.pika_shu;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
    public  static final int DATABASE_VERSION =85;
    public  static final String DATABASE = "DATABASE";
    private static final String TABLE_HEALTH_CENTER = "HealthCenter";
    private static final String TABLE_INVENTORY = "Inventory";
    private static final String TABLE_POKEMONS = "Pokemons";
    private static final String TABLE_WILD_POKEMON = "Wild";
    private static final String KEY_WILD_CURRENT_PV = "currentpv";
    private static final String TABLE_POKEMON_POSSEDE = "Possede";
    private static final String KEY_ID_POKEMON_POSSEDE = "idPossede";
    private static final String KEY_CURRENT_PV_POSSEDE = "currentpv";
    private static final String KEY_ID = "id";
    private static final String KEY_ID_INVENTORY = "idinv";
    private static final String KEY_ID_POKEMON = "idpoke";
    private static final String KEY_ID_WILD_POKEMON = "idwild";
    private static final String KEY_NAME_POKEMON = "namepoke";
    private static final String KEY_IMAGE_POKEMON = "imgpoke";
    private static final String KEY_TYPE1_POKEMON = "type1poke";
    private static final String KEY_TYPE2_POKEMON = "type2poke";
    private static final String KEY_HEIGHT_POKEMON = "hgtpoke";
    private static final String KEY_WEIGHT_POKEMON = "wgtpoke";
    private static final String KEY_NAME = "name";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_POKEMON_ID = "possedepokemonid";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LAT_WILD = "wildlat";
    private static final String KEY_LONG_WILD = "wildlong";
    private static final String KEY_PV = "pvpoke";
    private static final String KEY_FOUND = "foundpoke";
    private static final String KEY_LONG = "long";

    public DatabaseHandler(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOIN_TABLE = "CREATE TABLE " + TABLE_HEALTH_CENTER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LAT + " TEXT," + KEY_LONG + " TEXT)";
        db.execSQL(CREATE_SOIN_TABLE);
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + TABLE_INVENTORY + "("
                + KEY_ID_INVENTORY + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT," + KEY_QUANTITY + " INT)";
        db.execSQL(CREATE_INVENTORY_TABLE);
        String CREATE_POKEMON_TABLE = "CREATE TABLE " + TABLE_POKEMONS + "("
                + KEY_ID_POKEMON + " INTEGER PRIMARY KEY,"
                + KEY_NAME_POKEMON + " TEXT," + KEY_IMAGE_POKEMON + " STRING,"
                + KEY_TYPE1_POKEMON + " TEXT," + KEY_TYPE2_POKEMON + " TEXT,"
                + KEY_HEIGHT_POKEMON + " INT," + KEY_WEIGHT_POKEMON + " INT,"
                + KEY_PV + " INT," + KEY_FOUND + " BOOLEAN)";
        db.execSQL(CREATE_POKEMON_TABLE);
        String CREATE_WILD_TABLE = "CREATE TABLE " + TABLE_WILD_POKEMON + "("
                + KEY_ID_WILD_POKEMON + " INTEGER PRIMARY KEY,"
                + KEY_WILD_CURRENT_PV + " INT,"
                + KEY_LAT_WILD + " TEXT," + KEY_LONG_WILD + " TEXT)";
        db.execSQL(CREATE_WILD_TABLE);
        String CREATE_POSSEDE_TABLE = "CREATE TABLE " + TABLE_POKEMON_POSSEDE + "("
                + KEY_ID_POKEMON_POSSEDE + " INTEGER PRIMARY KEY,"
                +  KEY_CURRENT_PV_POSSEDE + " INTEGER,"
                + KEY_POKEMON_ID + " INTEGER)";
        db.execSQL(CREATE_POSSEDE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_CENTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POKEMONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WILD_POKEMON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POKEMON_POSSEDE);
        onCreate(db);
    }

    void addWild(wild wildPoke) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_WILD_POKEMON, wildPoke.getId());
        values.put(KEY_LAT_WILD, wildPoke.getlat());
        values.put(KEY_LONG_WILD, wildPoke.getdlong());
        db.insert(TABLE_WILD_POKEMON, null, values);
    }

    public List<wild> getAllWildPokemons() {
        List<wild> wildPokemonList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WILD_POKEMON, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(KEY_ID_WILD_POKEMON));
                @SuppressLint("Range") String lat = cursor.getString(cursor.getColumnIndex(KEY_LAT_WILD));
                @SuppressLint("Range") String lng = cursor.getString(cursor.getColumnIndex(KEY_LONG_WILD));
                wild pokemon = new wild(id, lat, lng);
                wildPokemonList.add(pokemon);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wildPokemonList;
    }

    void addPossede(possede possed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CURRENT_PV_POSSEDE, possed.getCurrentPv());
        values.put(KEY_POKEMON_ID, possed.getIdPokemon());

        try {
            db.insert(TABLE_POKEMON_POSSEDE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<possede> getAllPossede() {
        List<possede> possedeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_POKEMON_POSSEDE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                possede possed = new possede();
                possed.setID(Integer.parseInt(cursor.getString(0)));
                possed.setCurrentPv(Integer.parseInt(cursor.getString(1)));
                possed.setIDPokemon(Integer.parseInt(cursor.getString(2)));
                Log.d("DatabaseHandler", "ID: " + possed.getId() + ", Current PV: " + possed.getCurrentPv() + ", Pokemon ID: " + possed.getIdPokemon());
                possedeList.add(possed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return possedeList;
    }

    @SuppressLint("Range")
    public String getPokemonNameById(int pokemonId) {
        String pokemonName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POKEMONS, new String[]{KEY_NAME_POKEMON}, KEY_ID_POKEMON + "=?",
                new String[]{String.valueOf(pokemonId)}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                pokemonName = cursor.getString(cursor.getColumnIndex(KEY_NAME_POKEMON));
            }
            cursor.close();
        }
        return pokemonName;
    }

    public List<soin> getAllSoins() {
        List<soin> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_HEALTH_CENTER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                soin contact = new soin();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setLat(cursor.getString(1));
                contact.setLong(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    void addPokemon(Pokemon pokemon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_POKEMON, pokemon.getOrder());
        values.put(KEY_NAME_POKEMON, pokemon.getName());
        values.put(KEY_IMAGE_POKEMON, pokemon.getFrontResource());
        values.put(KEY_TYPE1_POKEMON, pokemon.getType1String());
        values.put(KEY_TYPE2_POKEMON, pokemon.getType2String());
        values.put(KEY_HEIGHT_POKEMON, pokemon.getHeight());
        values.put(KEY_WEIGHT_POKEMON, pokemon.getWeight());
        values.put(KEY_PV, pokemon.getPV());
        values.put(KEY_FOUND, pokemon.isFound());
        db.insert(TABLE_POKEMONS, null, values);
    }

    public List<Pokemon> getAllPokemons() {
        List<Pokemon> pokemonList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POKEMONS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("idpoke"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("namepoke"));
                int frontResource = cursor.getInt(cursor.getColumnIndexOrThrow("imgpoke"));
                String type1 = cursor.getString(cursor.getColumnIndexOrThrow("type1poke"));
                String type2 = cursor.getString(cursor.getColumnIndexOrThrow("type2poke"));
                Pokemon pokemon = new Pokemon(id, name, frontResource,
                        Enum.valueOf(Pokemon.POKEMON_TYPE.class, type1),
                        type2 == null ? null : Enum.valueOf(Pokemon.POKEMON_TYPE.class, type2));
                pokemonList.add(pokemon);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pokemonList;
    }

    public void addInventory(Inventory item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_QUANTITY, item.getQuantity());
        db.insert(TABLE_INVENTORY, null, values);
    }

    public void addItemToInventory(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (itemName.equals("soin")) {
            db.execSQL("UPDATE " + TABLE_INVENTORY + " SET " + KEY_QUANTITY + " = " + KEY_QUANTITY + " + 1 WHERE " + KEY_NAME + " = 'soin'");
        } else if (itemName.equals("pokeball")) {
            db.execSQL("UPDATE " + TABLE_INVENTORY + " SET " + KEY_QUANTITY + " = " + KEY_QUANTITY + " + 1 WHERE " + KEY_NAME + " = 'pokeball'");
        } else {
            System.out.println("Type d'élément non pris en charge.");
        }
    }

    public void removeItemFromInventory(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (itemName.equals("soin")) {
            db.execSQL("UPDATE " + TABLE_INVENTORY + " SET " + KEY_QUANTITY + " = " + KEY_QUANTITY + " - 1 WHERE " + KEY_NAME + " = 'soin'");
            db.execSQL("UPDATE " + TABLE_POKEMON_POSSEDE + " SET " + KEY_CURRENT_PV_POSSEDE + " = " + KEY_CURRENT_PV_POSSEDE + " + 50 WHERE " + KEY_ID_POKEMON_POSSEDE + " = 1");
        } else if (itemName.equals("pokeball")) {
            db.execSQL("UPDATE " + TABLE_INVENTORY + " SET " + KEY_QUANTITY + " = " + KEY_QUANTITY + " - 1 WHERE " + KEY_NAME + " = 'pokeball'");
        } else {
            System.out.println("Type d'élément non pris en charge.");
        }
    }

    @SuppressLint("Range")
    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_INVENTORY;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Inventory item = new Inventory();
                item.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_INVENTORY)));
                item.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                item.setQuantity(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY))));
                inventoryList.add(item);
            } while (cursor.moveToNext());
        }
        return inventoryList;
    }

    @SuppressLint("Range")
    public int getMyPokemonPv() {
        int currentPv = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POKEMON_POSSEDE, new String[]{KEY_CURRENT_PV_POSSEDE},
                KEY_ID_POKEMON_POSSEDE + "=?", new String[]{"1"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentPv = cursor.getInt(cursor.getColumnIndex(KEY_CURRENT_PV_POSSEDE));
            }
            cursor.close();
        }
        return currentPv;
    }

    public int getNumberOfItems(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT " + KEY_QUANTITY + " FROM " + TABLE_INVENTORY + " WHERE " + KEY_NAME + " = ?", new String[]{itemName});
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public void addHealthCenter(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LAT, String.valueOf(latitude));
        values.put(KEY_LONG, String.valueOf(longitude));
        db.insert(TABLE_HEALTH_CENTER, null, values);
        db.close();
    }
    @SuppressLint("Range")
    public int getPokemonIdByCoordinates(double latitude, double longitude) {
        SQLiteDatabase db = this.getReadableDatabase();
        int pokemonId = 1;
        String query = "SELECT " + KEY_ID_WILD_POKEMON + " FROM " + TABLE_WILD_POKEMON +
                " WHERE " + KEY_LAT_WILD + " = " + latitude +
                " AND " + KEY_LONG_WILD + " = " + longitude ;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            pokemonId = cursor.getInt(cursor.getColumnIndex(KEY_ID_WILD_POKEMON));
        }
        cursor.close();
        db.close();
        Log.d("teste", String.valueOf(pokemonId));
        return pokemonId;
    }

    @SuppressLint("Range")
    public HashMap<String, Object> getPokemonInfoByID(int pokemonId) {
        HashMap<String, Object> pokemonInfo = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POKEMONS, null, KEY_ID_POKEMON + "=?",
                new String[]{String.valueOf(pokemonId)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            pokemonInfo.put(KEY_ID_POKEMON, cursor.getInt(cursor.getColumnIndex(KEY_ID_POKEMON)));
            pokemonInfo.put(KEY_NAME_POKEMON, cursor.getString(cursor.getColumnIndex(KEY_NAME_POKEMON)));
            pokemonInfo.put(KEY_IMAGE_POKEMON, cursor.getString(cursor.getColumnIndex(KEY_IMAGE_POKEMON)));
            pokemonInfo.put(KEY_TYPE1_POKEMON, cursor.getString(cursor.getColumnIndex(KEY_TYPE1_POKEMON)));
            pokemonInfo.put(KEY_TYPE2_POKEMON, cursor.getString(cursor.getColumnIndex(KEY_TYPE2_POKEMON)));
            pokemonInfo.put(KEY_HEIGHT_POKEMON, cursor.getInt(cursor.getColumnIndex(KEY_HEIGHT_POKEMON)));
            pokemonInfo.put(KEY_WEIGHT_POKEMON, cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_POKEMON)));
            pokemonInfo.put(KEY_PV, cursor.getInt(cursor.getColumnIndex(KEY_PV)));
            pokemonInfo.put(KEY_FOUND, cursor.getInt(cursor.getColumnIndex(KEY_FOUND)) == 1);
            cursor.close();
        }
        db.close();
        return pokemonInfo;
    }

    @SuppressLint("Range")
    public int getPokemonPV(int pokemonId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int pokemonPV = -1;
        String[] columns = {KEY_PV};
        String selection = KEY_ID_POKEMON + "=?";
        String[] selectionArgs = {String.valueOf(pokemonId)};
        Cursor cursor = db.query(TABLE_POKEMONS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            pokemonPV = cursor.getInt(cursor.getColumnIndex(KEY_PV));
            cursor.close();
        }
        return pokemonPV;
    }

    @SuppressLint("Range")
    public int getPokemonCurrentPV(int pokemonId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int pokemonPV = -1;
        String[] columns = {KEY_WILD_CURRENT_PV};
        String selection = KEY_ID_WILD_POKEMON + "=?";
        String[] selectionArgs = {String.valueOf(pokemonId)};
        Cursor cursor = db.query(TABLE_WILD_POKEMON, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            pokemonPV = cursor.getInt(cursor.getColumnIndex(KEY_WILD_CURRENT_PV));
            cursor.close();
        }
        return pokemonPV;
    }

    public void updateWildPokemonPV(int pokemonId, int pv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WILD_CURRENT_PV, pv);
        String selection = KEY_ID_WILD_POKEMON + "=?";
        String[] selectionArgs = {String.valueOf(pokemonId)};
        db.update(TABLE_WILD_POKEMON, values, selection, selectionArgs);
    }

    public void removePV(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int pvToRemove = 24;
        db.execSQL("UPDATE " + TABLE_WILD_POKEMON + " SET " + KEY_WILD_CURRENT_PV + " = CASE WHEN " + KEY_WILD_CURRENT_PV + " > " + pvToRemove + " THEN " + KEY_WILD_CURRENT_PV + " - " + pvToRemove + " ELSE 0 END WHERE " + KEY_ID_WILD_POKEMON + " = " + id);
    }
    public void removemyPV() {
        SQLiteDatabase db = this.getWritableDatabase();
        int pvToRemove = 18;
        db.execSQL("UPDATE " + TABLE_POKEMON_POSSEDE + " SET " + KEY_CURRENT_PV_POSSEDE + " = CASE WHEN " + KEY_CURRENT_PV_POSSEDE + " > " + pvToRemove + " THEN " + KEY_CURRENT_PV_POSSEDE + " - " + pvToRemove + " ELSE 0 END WHERE " + KEY_ID_POKEMON_POSSEDE + " = " + 1);
    }
    public void setmyPV() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_POKEMON_POSSEDE + " SET " + KEY_CURRENT_PV_POSSEDE + " = 150 WHERE " + KEY_ID_POKEMON_POSSEDE + " = 1" );
    }
    public void removeWild(int pokemonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = KEY_ID_WILD_POKEMON + "=?";
        String[] selectionArgs = {String.valueOf(pokemonId)};
        db.delete(TABLE_WILD_POKEMON, selection, selectionArgs);
    }
}