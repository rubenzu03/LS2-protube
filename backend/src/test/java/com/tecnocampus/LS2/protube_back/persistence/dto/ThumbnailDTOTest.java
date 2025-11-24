package com.tecnocampus.LS2.protube_back.persistence.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThumbnailDTOTest {

    @Test
    void recordStoresValues() {
        ThumbnailDTO t = new ThumbnailDTO(12L, "thumb.jpg");
        assertEquals(12L, t.id());
        assertEquals("thumb.jpg", t.filename());
    }
}

