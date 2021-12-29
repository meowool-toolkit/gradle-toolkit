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
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.UnknownDomainObjectException
import org.gradle.kotlin.dsl.named
import kotlin.reflect.KClass

/**
 * Safe alternative to get the provided value of [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <T> NamedDomainObjectCollection<T>.getNamedOrNull(name: String): T? = try {
  named(name).orNull
} catch (e: UnknownDomainObjectException) {
  null
}

/**
 * Safe alternative to get the provided value of [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <T> NamedDomainObjectCollection<T>.getNamed(name: String): T = named(name).get()

/**
 * Safe alternative to get the provided value of [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <T> NamedDomainObjectCollection<T>.getNamed(
  name: String,
  configurationAction: Action<in T>,
): T = named(name, configurationAction).get()

/**
 * Safe alternative to [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <T> NamedDomainObjectCollection<T>.namedOrNull(name: String): NamedDomainObjectProvider<T>? = try {
  named(name)
} catch (e: UnknownDomainObjectException) {
  null
}

/**
 * Safe alternative to [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <T> NamedDomainObjectCollection<T>.configureNamed(
  name: String,
  configurationAction: Action<in T>,
) = try {
  named(name).configure(configurationAction)
} catch (e: UnknownDomainObjectException) {
  null
}

/**
 * Safe alternative to [NamedDomainObjectCollection.named].
 *
 * @author 凛 (RinOrz)
 */
fun <S : T, T : Any> NamedDomainObjectCollection<*>.configureNamed(
  name: String,
  type: KClass<S>,
  configurationAction: Action<in S>
) = try {
  named(name, type).configure(configurationAction)
} catch (e: UnknownDomainObjectException) {
  null
}
