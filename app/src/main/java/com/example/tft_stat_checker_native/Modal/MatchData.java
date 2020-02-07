package com.example.tft_stat_checker_native.Modal;

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
    private ArrayList<UnitData> units;
    private ArrayList<TraitData> traits;
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


    public MatchData(int placement, ArrayList<UnitData> units, ArrayList<TraitData> traits, int type, String id) {
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

    public ArrayList<TraitData> getTraits() {
        return traits;
    }

    public ArrayList<UnitData> getUnits() {
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

    public void setTraits(ArrayList<TraitData> traits) {
        this.traits = traits;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUnits(ArrayList<UnitData> units) {
        this.units = units;
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return placement == 0;
    }
}