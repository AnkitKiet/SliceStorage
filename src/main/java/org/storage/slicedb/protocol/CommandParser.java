package org.storage.slicedb.protocol;

import org.storage.slicedb.store.KeyValueStore;

public class CommandParser {

    public static String handle(String command, KeyValueStore store) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 0) return "ERR Empty Command";

        String action = parts[0].toUpperCase();
        return switch (action) {
            case "GET" -> {
                if (parts.length < 2) yield "ERR Missing key";
                String val = store.get(parts[1]);
                yield val != null ? "VALUE " + val : "NULL";
            }
            case "PUT" -> {
                if (parts.length < 3) yield "ERR Usage: PUT key value";
                store.put(parts[1], parts[2]);
                yield "OK";
            }
            case "DEL" -> {
                if (parts.length < 2) yield "ERR Missing key";
                store.delete(parts[1]);
                yield "DELETED";
            }
            default -> "ERR Unknown Command";
        };
    }
}