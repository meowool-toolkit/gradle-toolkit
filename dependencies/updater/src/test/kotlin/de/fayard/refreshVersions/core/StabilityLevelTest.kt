package de.fayard.refreshVersions.core

import de.fayard.refreshVersions.core.StabilityLevel.Alpha
import de.fayard.refreshVersions.core.StabilityLevel.Beta
import de.fayard.refreshVersions.core.StabilityLevel.Development
import de.fayard.refreshVersions.core.StabilityLevel.EarlyAccessProgram
import de.fayard.refreshVersions.core.StabilityLevel.Milestone
import de.fayard.refreshVersions.core.StabilityLevel.Preview
import de.fayard.refreshVersions.core.StabilityLevel.ReleaseCandidate
import de.fayard.refreshVersions.core.StabilityLevel.Snapshot
import de.fayard.refreshVersions.core.StabilityLevel.Stable
import de.fayard.refreshVersions.core.StabilityLevel.Unknown
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StabilityLevelTest {

    @Test
    fun `Test stability level comparisons`() {
        assertTrue(StabilityLevel.values().minBy { it } == Stable)

        testStabilityLevels(lessStable = Unknown, mostStable = Snapshot)
        testStabilityLevels(lessStable = Snapshot, mostStable = Preview)
        testStabilityLevels(lessStable = Preview, mostStable = Development)
        testStabilityLevels(lessStable = Development, mostStable = Alpha)
        testStabilityLevels(lessStable = Alpha, mostStable = Beta)
        testStabilityLevels(lessStable = Beta, mostStable = EarlyAccessProgram)
        testStabilityLevels(lessStable = EarlyAccessProgram, mostStable = Milestone)
        testStabilityLevels(lessStable = Milestone, mostStable = ReleaseCandidate)
        testStabilityLevels(lessStable = ReleaseCandidate, mostStable = Stable)

        @Suppress("RemoveRedundantQualifierName")
        StabilityLevel.values().forEach { Stable isAtLeastAsStableAs it }
    }

    private fun testStabilityLevels(lessStable: StabilityLevel, mostStable: StabilityLevel) {
        assertTrue(lessStable isLessStableThan mostStable)
        assertTrue(mostStable isMoreStableThan  lessStable)
        assertTrue(mostStable isAtLeastAsStableAs lessStable)
        assertTrue(mostStable < lessStable)

        assertFalse(mostStable isLessStableThan lessStable)
        assertFalse(lessStable isMoreStableThan  mostStable)
        assertFalse(lessStable isAtLeastAsStableAs mostStable)
    }
}
