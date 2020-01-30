package com.example.tft_stat_checker_native;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MatchData{
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_FOOTER = 3;

    // original json data
    private JSONObject json;

    // my data displayed on the card
    private int placement;
    private ArrayList<Unit> units;
    private ArrayList<Trait> traits;
    private int type;
    private String id;
    private String loadingStatus;

    // match data from json
    private int setNumber;
    private int queueID;
    private long gameDateTime;
    private double gameLength;
    private ArrayList<String> participantsID;
    private ArrayList<ParticipantData> participants;


    public MatchData(int placement, ArrayList<Unit> units, ArrayList<Trait> traits, int type, String id) {
        this.placement = placement;
        this.units = units;
        this.traits = traits;
        this.type = type;
        this.id = id;
        this.loadingStatus = "";
    }

    public MatchData(String id) {
        this.placement = 0;
        this.units = new ArrayList<>();
        this.traits = new ArrayList<>();
        this.type = MatchData.TYPE_CONTENT;
        this.id = id;
        this.loadingStatus = "";
    }

    public MatchData(JSONObject data) throws JSONException{
        JSONObject info = data.getJSONObject("info");
        JSONObject metadata = data.getJSONObject("metadata");

        json = data;

        setNumber = info.getInt("tft_set_number");
        queueID = info.getInt("queue_id");
        gameDateTime = info.getLong("game_datetime");
        gameLength = info.getDouble("game_length");

        JSONArray idList = metadata.getJSONArray("participants");
        participantsID = new ArrayList<>();
        for (int i = 0; i < idList.length(); i++) {
            participantsID.add(idList.getString(i));
        }

        JSONArray participantsList = info.getJSONArray("participants");
        participants = new ArrayList<>();
        for (int i = 0; i < idList.length(); i++) {
            ParticipantData participant = new ParticipantData(participantsList.getJSONObject(i));
            participants.add(participant);
        }
    }

    public void loadData(JSONObject data, String puuid) throws JSONException {
        JSONObject info = data.getJSONObject("info");
        JSONObject metadata = data.getJSONObject("metadata");

        json = data;

        setNumber = info.getInt("tft_set_number");
        queueID = info.getInt("queue_id");
        gameDateTime = info.getLong("game_datetime");
        gameLength = info.getDouble("game_length");

        JSONArray idList = metadata.getJSONArray("participants");
        participantsID = new ArrayList<>();
        for (int i = 0; i < idList.length(); i++) {
            participantsID.add(idList.getString(i));
        }

        JSONArray participantsList = info.getJSONArray("participants");
        participants = new ArrayList<>();
        for (int i = 0; i < idList.length(); i++) {
            ParticipantData participant = new ParticipantData(participantsList.getJSONObject(i));
            if (participant.getPuuid().equals(puuid)) {
                this.placement = participant.getPlacement();
                this.units = participant.getUnits();
                this.traits = participant.getTraits();
            }
            participants.add(participant);
        }

        this.loadingStatus = "LOADED";
    }

    public JSONObject getJson() {
        return json;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getQueueID() {
        return queueID;
    }

    public String getLoadingStatus() {
        return loadingStatus;
    }

    public void setLoadingStatus(String loadingStatus) {
        this.loadingStatus = loadingStatus;
    }

    public long getGameDateTime() {
        return gameDateTime;
    }

    public double getGameLength() {
        return gameLength;
    }

    public String getGameDateTimeString() {
        Date date = new Date(gameDateTime);
        Date now = new Date();

        long diff = now.getTime() - date.getTime();
        long diffInSec = diff / 1000;
        if (diffInSec < 30) {
            return "less than a minute ago";
        } else if (diffInSec < 90) {
            return "a minute ago";
        } else if (diffInSec < 2670) {
            return diffInSec / 60 + " minutes ago";
        } else if (diffInSec < 5370) {
            return "about an hour ago";
        } else if (diffInSec < 86370) {
            return "about " + diffInSec / 3600 + " hours ago";
        } else if (diffInSec < 151170) {
            return "about a day ago";
        } else if (diffInSec < 2591970) {
            return diffInSec / 86400 + " days ago";
        } else {
            return "more than a month ago";
        }
    }

    public String getGameLengthString() {
        int durationInSecs = (int) gameLength;
        return durationInSecs / 60 + " minutes";
    }

    public ArrayList<String> getParticipantsID() {
        return participantsID;
    }

    public ArrayList<ParticipantData> getParticipants() {
        return participants;
    }

    public ArrayList<Trait> getTraits() {
        return traits;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public int getPlacement() {
        return placement;
    }

    public int getType() {
        return type;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public void setTraits(ArrayList<Trait> traits) {
        this.traits = traits;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return placement == 0;
    }
}

class ParticipantData {
    private int goldLeft;
    private int lastRound;
    private int level;
    private int placement;
    private int playersEliminated;
    private String puuid;
    private double timeEliminated;
    private int totalDamageToPlayers;
    private ArrayList<Unit> units;
    private ArrayList<Trait> traits;

    private SummonerData summonerData;
    private SummonerRankedData summonerRankedData;

    public ParticipantData(JSONObject participant) throws JSONException {
        this.goldLeft = participant.getInt("gold_left");
        this.lastRound = participant.getInt("last_round");
        this.level = participant.getInt("level");
        this.placement = participant.getInt("placement");
        this.playersEliminated = participant.getInt("players_eliminated");
        this.puuid = participant.getString("puuid");
        this.timeEliminated = participant.getDouble("time_eliminated");
        this.totalDamageToPlayers = participant.getInt("total_damage_to_players");
        this.units = new ArrayList<>();

        JSONArray units = participant.getJSONArray("units");
        for (int i = 0; i < units.length(); i++) {
            this.units.add(new Unit(units.getJSONObject(i)));
        }

        JSONArray traits = participant.getJSONArray("traits");
        this.traits = new ArrayList<>();
        for (int i = 0; i < traits.length(); i++) {
            this.traits.add(new Trait(traits.getJSONObject(i)));
        }
        this.traits.sort((Trait a, Trait b) -> b.getStyle() - a.getStyle());
    }

    public void loadSummonerData(SummonerData summonerData, SummonerRankedData summonerRankedData) {
        this.summonerData = summonerData;
        this.summonerRankedData = summonerRankedData;
    }

    public SummonerData getSummonerData() { return summonerData; }
    public SummonerRankedData getSummonerRankedData() { return summonerRankedData; }
    public int getGoldLeft() {
        return goldLeft;
    }
    public int getLastRound() {
        return lastRound;
    }
    public int getLevel() {
        return level;
    }
    public int getPlacement() {
        return placement;
    }
    public int getPlayersEliminated() {
        return playersEliminated;
    }
    public String getPuuid() {
        return puuid;
    }
    public double getTimeEliminated() {
        return timeEliminated;
    }
    public int getTotalDamageToPlayers() {
        return totalDamageToPlayers;
    }
    public ArrayList<Unit> getUnits() {
        return units;
    }
    public ArrayList<Trait> getTraits() {
        return traits;
    }
}

class Unit {
    private String characterID;
    private String name;
    private int rarity;
    private int tier;
    private ArrayList<Item> items;

    public Unit(String characterID, String name, int rarity, int tier, ArrayList<Item> items) {
        this.characterID = characterID;
        this.name = name;
        this.rarity = rarity;
        this.tier = tier;
        this.items = items;
    }

    public Unit(JSONObject unit) throws JSONException{
        this.characterID = unit.getString("character_id");
        this.name = unit.getString("name");
        this.rarity = unit.getInt("rarity");
        this.tier = unit.getInt("tier");
        this.items = new ArrayList<>();

        JSONArray itemList = unit.getJSONArray("items");
        for (int i = 0; i < itemList.length(); i++) {
            this.items.add(new Item(itemList.getInt(i)));
        }
    }

    public String getCharacterID() {
        return characterID;
    }
    public String getName() {
        return name;
    }
    public int getRarity() {
        return rarity;
    }
    public int getTier() {
        return tier;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
}

class Trait {
    private String name;
    private int numUnits;
    private int style;
    private int tierCurrent;
    private int tierTotal;

    public Trait(String name, int numUnits, int style, int tierCurrent, int tierTotal) {
        this.name = name;
        this.numUnits = numUnits;
        this.style = style;
        this.tierCurrent = tierCurrent;
        this.tierTotal = tierTotal;
    }

    public Trait(JSONObject trait) throws JSONException{
        this.name = trait.getString("name");
        this.numUnits = trait.getInt("num_units");
        this.style = trait.getInt("style");
        this.tierCurrent = trait.getInt("tier_current");
        this.tierTotal = trait.getInt("tier_total");
    }

    public String getName() {
        return name;
    }
    public int getNumUnits() {
        return numUnits;
    }
    public int getStyle() {
        return style;
    }
    public int getTierCurrent() {
        return tierCurrent;
    }
    public int getTierTotal() {
        return tierTotal;
    }
}

class Item {
    private int id;

    public Item(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}