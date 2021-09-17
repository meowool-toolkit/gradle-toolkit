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
@file:Suppress("SpellCheckingInspection")

import com.android.build.gradle.BaseExtension

/**
 * Contains the C [flags] of each obfuscator library.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
sealed class NdkObfuscator(val flags: List<String>) {

  /* https://github.com/GoSSIP-SJTU/Armariris */
  class Armariris(
    flatten: Boolean = true,
    split: Boolean = true,
    splitLoop: Int = 1,
    insnSubstitution: Boolean = true,
    insnSubstitutionLoop: Int = 1,
    bogusControlFlow: Boolean = true,
    bogusControlFlowLoop: Int = 1,
    bogusControlFlowProbability: Int = 30,
    stringObfuscate: Boolean = true,
    seed: Long = 0xdeadbeaf,
  ) : NdkObfuscator(
    listOfNotNull(
      "-mllvm -fla".takeIf { flatten },
      "-mllvm -split".takeIf { split },
      "-mllvm -split_num=$splitLoop".takeIf { split },
      "-mllvm -sub".takeIf { insnSubstitution },
      "-mllvm -sub_loop=$insnSubstitutionLoop".takeIf { insnSubstitution },
      "-mllvm -bcf".takeIf { bogusControlFlow },
      "-mllvm -bcf_loop=$bogusControlFlowLoop".takeIf { bogusControlFlow },
      "-mllvm -bcf_prob=$bogusControlFlowProbability".takeIf { bogusControlFlow },
      "-mllvm -sobf".takeIf { stringObfuscate },
      "-mllvm -seed=$seed",
    )
  )

  /* https://github.com/amimo/goron */
  class Goron(
    flatten: Boolean = true,
    indirectJump: Boolean = true,
    indirectCall: Boolean = true,
    indirectGetVariable: Boolean = true,
    stringObfuscate: Boolean = true,
  ) : NdkObfuscator(
    listOfNotNull(
      "-mllvm -irobf-cff".takeIf { flatten },
      "-mllvm -irobf-indbr".takeIf { indirectJump },
      "-mllvm -irobf-icall".takeIf { indirectCall },
      "-mllvm -irobf-indgv".takeIf { indirectGetVariable },
      "-mllvm -irobf-cse".takeIf { stringObfuscate }
    )
  )

  /* https://github.com/HikariObfuscator/Hikari/ */
  class Hikari(
    flatten: Boolean = true,
    split: Boolean = true,
    functionCallObfuscate: Boolean = true,
    functionWrapper: Boolean = true,
    functionWrapperProbability: Int = 10,
    functionWrapperLoop: Int = 2,
    antiClassDump: Boolean = true,
    insnSubstitution: Boolean = true,
    insnSubstitutionLoop: Int = 1,
    bogusControlFlow: Boolean = true,
    bogusControlFlowLoop: Int = 1,
    bogusControlFlowProbability: Int = 30,
    stringObfuscate: Boolean = true,
    indirectBranching: Boolean = true,
  ) : NdkObfuscator(
    listOfNotNull(
      "-mllvm -enable-fco".takeIf { functionCallObfuscate },
      "-mllvm -enable-cffobf".takeIf { flatten },
      "-mllvm -enable-splitobf".takeIf { split },
      "-mllvm -enable-bcfobf".takeIf { bogusControlFlow },
      "-mllvm -bcf_loop=$bogusControlFlowLoop".takeIf { bogusControlFlow },
      "-mllvm -bcf_prob=$bogusControlFlowProbability".takeIf { bogusControlFlow },
      "-mllvm -enable-subobf".takeIf { insnSubstitution },
      "-mllvm -sub_loop=$insnSubstitutionLoop".takeIf { insnSubstitution },
      "-mllvm -enable-funcwra".takeIf { functionWrapper },
      "-mllvm -fw_prob=$functionWrapperProbability".takeIf { functionWrapper },
      "-mllvm -fw_times=$functionWrapperLoop".takeIf { functionWrapper },
      "-mllvm -enable-acdobf".takeIf { antiClassDump },
      "-mllvm -enable-strcry".takeIf { stringObfuscate },
      "-mllvm -enable-indibran".takeIf { indirectBranching }
    )
  )
}

fun BaseExtension.enableNdkObfuscate(obfuscator: NdkObfuscator) {
  defaultConfig {
    externalNativeBuild.cmake {
      cFlags += obfuscator.flags + listOf("-fvisibility=hidden", "-fvisibility-inlines-hidden")
      cppFlags += obfuscator.flags + "-fvisibility=hidden"
    }
  }
}
