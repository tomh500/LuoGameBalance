package cn.luotiany1.luogamebalance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class XpTest {
    @Test void levelBoundariesMatchVanillaCurve() {
        assertEquals(0, Xp.atLevel(0));
        assertEquals(352, Xp.atLevel(16));
        assertEquals(394, Xp.atLevel(17));
        assertEquals(1507, Xp.atLevel(31));
        assertEquals(1628, Xp.atLevel(32));
    }
    @Test void nextLevelCurveMatchesVanilla() {
        assertEquals(7, Xp.toNext(0));
        assertEquals(37, Xp.toNext(15));
        assertEquals(42, Xp.toNext(16));
        assertEquals(112, Xp.toNext(30));
        assertEquals(121, Xp.toNext(31));
    }
}
