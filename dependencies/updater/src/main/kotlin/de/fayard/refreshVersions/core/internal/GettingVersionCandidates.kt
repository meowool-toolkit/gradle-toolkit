/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 */
package de.fayard.refreshVersions.core.internal

import de.fayard.refreshVersions.core.DependencyVersionsFetcher
import de.fayard.refreshVersions.core.Version
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode.FilterMode.AllIntermediateVersions
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode.FilterMode.Latest
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode.FilterMode.LatestByStabilityLevel
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode.SortingMode.ByRepo
import de.fayard.refreshVersions.core.internal.VersionCandidatesResultMode.SortingMode.ByVersion
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun List<DependencyVersionsFetcher>.getVersionCandidates(
  currentVersion: Version,
  resultMode: VersionCandidatesResultMode
): List<Version> {

  val results = getVersionCandidates(versionFilter = { it > currentVersion })
  return when (resultMode.filterMode) {
    AllIntermediateVersions -> when (resultMode.sortingMode) {
      is ByRepo -> results.byRepoSorting(resultMode.sortingMode).flatMap {
        it.availableVersions.asSequence()
      }
      ByVersion -> results.flatMap { it.availableVersions.asSequence() }.sorted()
    }.distinct().toList()
    LatestByStabilityLevel -> when (resultMode.sortingMode) {
      is ByRepo -> results.byRepoSorting(resultMode.sortingMode).flatMap {
        it.availableVersions.filterLatestByStabilityLevel().asSequence()
      }
      ByVersion -> results.flatMap {
        it.availableVersions.filterLatestByStabilityLevel().asSequence()
      }.sorted()
    }.distinct().toList()
    Latest -> when (resultMode.sortingMode) {
      is ByRepo -> results.byRepoSorting(resultMode.sortingMode).map {
        it.availableVersions.last()
      }.distinct().toList()
      is ByVersion -> listOf(results.flatMap { it.availableVersions.asSequence() }.sorted().last())
    }
  }
}

private fun Sequence<DependencyVersionsFetcher.SuccessfulResult>.byRepoSorting(
  sortingMode: ByRepo
): Sequence<DependencyVersionsFetcher.SuccessfulResult> = when (sortingMode) {
  ByRepo -> this
  ByRepo.LastUpdated -> sortedBy { it.lastUpdateTimestampMillis }
  ByRepo.LastVersionComparison -> sortedBy { it.availableVersions.last() }
}

private fun List<Version>.filterLatestByStabilityLevel(): List<Version> {
  return sortedDescending().fold<Version, List<Version>>(emptyList()) { acc, versionCandidate ->
    val previousStabilityLevel = acc.lastOrNull()?.stabilityLevel
      ?: return@fold acc + versionCandidate
    if (versionCandidate.stabilityLevel isMoreStableThan previousStabilityLevel) {
      acc + versionCandidate
    } else acc
  }.asReversed()
}

private suspend fun List<DependencyVersionsFetcher>.getVersionCandidates(
  versionFilter: ((Version) -> Boolean)? = null
): Sequence<DependencyVersionsFetcher.SuccessfulResult> {

  require(isNotEmpty()) { "Cannot get version candidates with an empty fetchers list." }
  val moduleId = first().moduleId
  require(all { it.moduleId == moduleId })

  return coroutineScope {
    map { fetcher ->
      async {
        @Suppress("BlockingMethodInNonBlockingContext") // False positive.
        fetcher.getAvailableVersionsOrNull(versionFilter = versionFilter)
      }
    }
  }.awaitAll().filterNotNull().also { results ->
    if (results.isEmpty()) throw NoSuchElementException(
      buildString {
        append("$moduleId not found. ")
        appendln("Searched the following repositories:")
        this@getVersionCandidates.forEach { appendln("- ${it.repoKey}") }
      }
    )
  }.distinctBy {
    it.availableVersions
  }.asSequence()
}
