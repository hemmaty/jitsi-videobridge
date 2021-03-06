/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.videobridge.cc

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldContainExactly
import org.jitsi.nlj.MediaSourceDesc
import org.jitsi.videobridge.VideoConstraints

class BitrateControllerTest : ShouldSpec() {
    override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

    init {

        context("Effective constraints") {
            val conferenceEndpoints = List(5) { i -> Endpoint("endpoint-${i + 1}") }

            context("When nothing is specified (expect 180p)") {
                val lastN = -1
                val videoConstraints = mapOf("endpoint-1" to VideoConstraints(720))

                BitrateController.makeEndpointMultiRankList(conferenceEndpoints, videoConstraints, lastN).map {
                    it.endpoint.id to it.effectiveVideoConstraints
                }.toMap().shouldContainExactly(
                    mapOf(
                        "endpoint-1" to VideoConstraints(720),
                        "endpoint-2" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-3" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-4" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-5" to VideoConstraints.thumbnailVideoConstraints
                    )
                )
            }

            context("With LastN") {
                val lastN = 3
                val videoConstraints = mapOf<String, VideoConstraints>()

                BitrateController.makeEndpointMultiRankList(conferenceEndpoints, videoConstraints, lastN).map {
                    it.endpoint.id to it.effectiveVideoConstraints
                }.toMap().shouldContainExactly(
                    mapOf(
                        "endpoint-1" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-2" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-3" to VideoConstraints.thumbnailVideoConstraints,
                        "endpoint-4" to VideoConstraints.disabledVideoConstraints,
                        "endpoint-5" to VideoConstraints.disabledVideoConstraints
                    )
                )
            }
        }
    }
}

class Endpoint(
    val id: String,
    val mediaSources_: Array<MediaSourceDesc> = arrayOf()
) : BitrateController.MediaSourceContainer {
    override fun getID() = id
    override fun getMediaSources() = mediaSources_
}
