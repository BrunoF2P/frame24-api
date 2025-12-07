package com.frame24.api.operations.application.service;

import com.frame24.api.operations.application.dto.BatchCreateSeatsRequest.RowNamingPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Teste unitário simples para validar lógica de nomenclatura de fileiras.
 */
class RowNamingTest {

    @Test
    void testAlphabeticNaming() {
        Assertions.assertEquals("A", generateRowCode(0, 10, RowNamingPattern.ALPHABETIC));
        Assertions.assertEquals("B", generateRowCode(1, 10, RowNamingPattern.ALPHABETIC));
        Assertions.assertEquals("J", generateRowCode(9, 10, RowNamingPattern.ALPHABETIC));
    }

    @Test
    void testReverseAlphabeticNaming() {
        // 10 linhas (0-9). 0 -> J, 9 -> A
        Assertions.assertEquals("J", generateRowCode(0, 10, RowNamingPattern.REVERSE_ALPHABETIC)); // Top row (screen)
        Assertions.assertEquals("A", generateRowCode(9, 10, RowNamingPattern.REVERSE_ALPHABETIC)); // Back row
    }

    private String generateRowCode(int rowIndex, int totalRows, RowNamingPattern pattern) {
        if (pattern == RowNamingPattern.NUMERIC) {
            return String.valueOf(rowIndex + 1);
        }
        int index = (pattern == RowNamingPattern.REVERSE_ALPHABETIC)
                ? (totalRows - 1 - rowIndex)
                : rowIndex;
        return index >= 0 && index < 26 ? String.valueOf((char) ('A' + index)) : String.valueOf(index);
    }
}
