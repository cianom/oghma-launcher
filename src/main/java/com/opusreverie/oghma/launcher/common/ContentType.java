package com.opusreverie.oghma.launcher.common;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Specifies the different pack file types, the file extensions
 * they're identified by and the paths they should be extracted to.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public enum ContentType {

    FORM("sf", "schema/form"),
    MATERIAL("sm", "schema/material"),
    CLIMATE("sc", "schema/climate"),
    ITEM("si", "schema/item"),
    FLORA("sr", "schema/flora"),
    GRASS("sg", "schema/grass"),
    MUSIC("am", "audio/music"),
    ENVIRONMENT("an", "audio/environment"),
    EFFECT("ae", "audio/effect"),
    UI("gu", "graphic/ui"),
    GAME("gg", "graphic/game"),
    SHADER("gs", "graphic/shader");


    private final String extension;
    private final String contentPath;

    ContentType(String extension, String contentPath) {
        this.extension = extension;
        this.contentPath = contentPath;
    }

    public static Optional<ContentType> fromFilePath(final String fileName) {
        final int i = fileName.lastIndexOf('.');
        if (i > 0 && fileName.length() - i >= 3) {
            String extChars = fileName.substring(i + 1, i + 3);
            return EnumSet.allOf(ContentType.class).stream()
                    .filter(type -> type.getExtension().equalsIgnoreCase(extChars))
                    .findFirst();
        }
        return Optional.empty();
    }

    public String getExtension() {
        return extension;
    }

    public String getContentPath() {
        return contentPath;
    }
}
