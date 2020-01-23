package com.example.tft_stat_checker_native;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

public class BitMapManager {
    private static HashMap<String, Bitmap> storage = new HashMap<>();

    public static void initialize(Context ctx) {
        if (storage.size() == 0) {
            // traits
            Bitmap alchemist = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.alchemist);
            storage.put("alchemist", alchemist);
            Bitmap avatar = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.avatar);
            storage.put("avatar", avatar);
            Bitmap berserker = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.berserker);
            storage.put("berserker", berserker);
            Bitmap crystal = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.crystal);
            storage.put("crystal", crystal);
            Bitmap desert = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.desert);
            storage.put("desert", desert);
            Bitmap electric = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.electric);
            storage.put("electric", electric);
            Bitmap inferno = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.inferno);
            storage.put("inferno", inferno);
            Bitmap light = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.light);
            storage.put("light", light);
            Bitmap mage = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.mage);
            storage.put("mage", mage);
            Bitmap mountain = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.mountain);
            storage.put("mountain", mountain);
            Bitmap mystic = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.mystic);
            storage.put("mystic", mystic);
            Bitmap ocean = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ocean);
            storage.put("ocean", ocean);
            Bitmap poison = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.poison);
            storage.put("poison", poison);
            Bitmap predator = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.predator);
            storage.put("predator", predator);
            Bitmap set2_assassin = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.set2_assassin);
            storage.put("set2_assassin", set2_assassin);
            Bitmap set2_blademaster = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.set2_blademaster);
            storage.put("set2_blademaster", set2_blademaster);
            Bitmap set2_glacial = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.set2_glacial);
            storage.put("set2_glacial", set2_glacial);
            Bitmap set2_ranger = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.set2_ranger);
            storage.put("set2_ranger", set2_ranger);
            Bitmap shadow = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.shadow);
            storage.put("shadow", shadow);
            Bitmap soulbound = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.soulbound);
            storage.put("soulbound", soulbound);
            Bitmap steel = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.metal);
            storage.put("metal", steel);
            Bitmap summoner = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.summoner);
            storage.put("summoner", summoner);
            Bitmap warden = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.warden);
            storage.put("warden", warden);
            Bitmap wind = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.wind);
            storage.put("wind", wind);
            Bitmap woodland = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.woodland);
            storage.put("woodland", woodland);

            //champions
        }
    }

    public static Bitmap getBitMapByKey(String key) {
        return storage.get(key);
    }
}
