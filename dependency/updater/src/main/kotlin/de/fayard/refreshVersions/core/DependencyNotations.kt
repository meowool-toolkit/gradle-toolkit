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

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
package de.fayard.refreshVersions.core

import de.fayard.refreshVersions.core.internal.ArtifactVersionKeyRule
import de.fayard.refreshVersions.core.internal.InternalRefreshVersionsApi

@RequiresOptIn
private annotation class PrivateForImplementation

interface DependencyNotation : CharSequence {
  fun withVersionPlaceholder(): String
  fun withVersion(version: String): String
  fun withoutVersion(): String
  operator fun invoke(version: String?): String

  override fun toString(): String

  @PrivateForImplementation
  fun attachToGroup(dependencyGroup: AbstractDependencyGroup) {
    throw UnsupportedOperationException()
  }

  @PrivateForImplementation
  val externalImplementationGuard: Nothing

  companion object {
    operator fun invoke(
      group: String,
      name: String,
      isBom: Boolean = false,
      usePlatformConstraints: Boolean? = null
    ): DependencyNotation = DependencyNotationImpl(
      group = group,
      name = name,
      isBom = isBom,
      usePlatformConstraints = usePlatformConstraints
    )
  }
}

@OptIn(PrivateForImplementation::class)
private class DependencyNotationImpl(
  group: String,
  name: String,
  private val isBom: Boolean,
  private val usePlatformConstraints: Boolean?
) : DependencyNotation {

  override fun withVersionPlaceholder(): String = this(version = "_")
  override fun withVersion(version: String): String = this(version = version)
  override fun withoutVersion(): String = this(version = null)

  override operator fun invoke(version: String?): String = when (version) {
    null -> artifactPrefix
    else -> "$artifactPrefix:$version"
  }

  private fun shouldUsePlatformConstraints(): Boolean {
    val dependencyGroup = dependencyGroup ?: return usePlatformConstraints ?: false
    if (AbstractDependencyGroup.disableBomCheck.not()) {
      if (isBom) {
        if (dependencyGroup.usedDependencyNotationsWithNoPlatformConstraints) error(
          "You are trying to use a BoM ($artifactPrefix), " +
            "but dependency notations relying on it have been declared before! " +
            "Declare the BoM first to fix this issue."
        )
        dependencyGroup.usePlatformConstraints = true
      } else when (usePlatformConstraints) {
        null -> {
          if (dependencyGroup.usePlatformConstraints.not()) {
            dependencyGroup.usedDependencyNotationsWithNoPlatformConstraints = true
          }
        }
      }
    }

    return usePlatformConstraints ?: dependencyGroup.usePlatformConstraints
  }

  @PrivateForImplementation
  override fun attachToGroup(dependencyGroup: AbstractDependencyGroup) {
    check(this.dependencyGroup == null)
    this.dependencyGroup = dependencyGroup
  }

  private var dependencyGroup: AbstractDependencyGroup? = null

  private val artifactPrefix = "$group:$name"

  override val length get() = toString().length
  override fun get(index: Int) = toString()[index]
  override fun subSequence(
    startIndex: Int,
    endIndex: Int
  ) = toString().subSequence(startIndex = startIndex, endIndex = endIndex)

  override fun toString(): String = artifactPrefix + if (shouldUsePlatformConstraints()) "" else ":_"

  @PrivateForImplementation
  override val externalImplementationGuard: Nothing
    get() = throw IllegalAccessException()
}

@OptIn(PrivateForImplementation::class)
sealed class AbstractDependencyGroup(
  val group: String,
  rawRule: String? = null,
  var usePlatformConstraints: Boolean = false
) {

  private val usePlatformConstraintsInitialValue = usePlatformConstraints

  private val rule: ArtifactVersionKeyRule? = rawRule?.let {
    val lines = it.lines()
    assert(lines.size == 2) {
      "2 lines were expected, but ${lines.size} were found: $it"
    }
    ArtifactVersionKeyRule(
      artifactPattern = lines.first(),
      versionKeyPattern = lines.last()
    )
  }

  companion object {
    private val ALL = mutableListOf<AbstractDependencyGroup>()

    @InternalRefreshVersionsApi
    val ALL_RULES: List<ArtifactVersionKeyRule>
      get() = ALL.mapNotNull { it.rule }

    @InternalRefreshVersionsApi
    var disableBomCheck: Boolean = false
  }

  init {
    assert(group.isNotBlank()) { "Group shall not be blank" }
    ALL.add(this)
  }

  fun module(
    name: String,
    isBom: Boolean = false,
    usePlatformConstraints: Boolean? = if (isBom) false else null
  ): DependencyNotation {
    assert(name.trimStart() == name) { "module($name) has superfluous leading whitespace" }
    assert(name.trimEnd() == name) { "module($name) has superfluous trailing whitespace" }
    assert(name.contains(":").not()) { "module($name) is invalid" }
    return DependencyNotationImpl(
      group = group,
      name = name,
      isBom = isBom,
      usePlatformConstraints = usePlatformConstraints
    ).also {
      it.attachToGroup(this@AbstractDependencyGroup)
    }
  }

  @InternalRefreshVersionsApi
  fun reset() {
    usedDependencyNotationsWithNoPlatformConstraints = false
    usePlatformConstraints = usePlatformConstraintsInitialValue
  }

  @PrivateForImplementation
  internal var usedDependencyNotationsWithNoPlatformConstraints = false
}
