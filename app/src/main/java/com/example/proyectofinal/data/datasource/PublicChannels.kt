package com.example.proyectofinal.data.datasource

import com.example.proyectofinal.data.model.Movie

object PublicChannels {

    fun getChannels(): List<Movie> {
        val channels = mutableListOf<Movie>()

        // Chile
        channels.add(Movie(
            id = "cl_tvu",
            title = "TVU HD",
            logo = "https://i.imgur.com/XHChUjI.png",
            url = "http://unlimited6-cl.dps.live/tvu/tvu.smil/playlist.m3u8",
            category = "Chile"
        ))
        channels.add(Movie(
            id = "cl_sporting",
            title = "Sporting HD",
            logo = "https://i.imgur.com/KO9XhLU.png",
            url = "http://unlimited6-cl.dps.live/sportinghd/sportinghd.smil/playlist.m3u8",
            category = "Chile" // Mapped from Deportes/Chile context
        ))
        channels.add(Movie(
            id = "cl_senado",
            title = "TV Senado HD",
            logo = "https://i.imgur.com/ozCnnDg.png",
            url = "http://janus-tv-ply.senado.cl/playlist/stream.m3u8?s=tvsenado-hd",
            category = "Chile"
        ))

        // Argentina
        channels.add(Movie(
            id = "arg_mitre",
            title = "Radio Mitre TV",
            logo = "https://i.imgur.com/uvck4Vj.png",
            url = "https://live-05-13-mitre.vodgc.net/live-05-13-mitre/index.m3u8",
            category = "Argentina"
        ))
        channels.add(Movie(
            id = "arg_telenord",
            title = "Telenord",
            logo = "https://i.imgur.com/yqXORkt.png",
            url = "https://617c5175c970b.streamlock.net:4444/previsoratv/live/playlist.m3u8",
            category = "Argentina"
        ))
        channels.add(Movie(
            id = "arg_realpolitik",
            title = "Radio Realpolitik",
            logo = "https://i.imgur.com/L9KCdTX.png",
            url = "https://vivo.solumedia.com:19360/realpolitik/realpolitik.m3u8",
            category = "Argentina"
        ))
        channels.add(Movie(
            id = "arg_radiocanal",
            title = "Radiocanal",
            logo = "https://i.imgur.com/NNyDWsz.png",
            url = "http://204.199.3.2/.m3u8",
            category = "Argentina"
        ))

        // Peru
        channels.add(Movie(
            id = "pe_america",
            title = "América TV",
            logo = "https://i.imgur.com/S7FMr6G.png",
            url = "https://alba-pe-atv-atv.stream.mediatiquestream.com/index.m3u8",
            category = "Perú"
        ))
        channels.add(Movie(
            id = "pe_atv",
            title = "ATV",
            logo = "https://i.imgur.com/RZLkfJe.png",
            url = "https://alba-pe-atv-atvmas.stream.mediatiquestream.com/index.m3u8",
            category = "Perú"
        ))
         channels.add(Movie(
            id = "pe_ovacion",
            title = "Ovación TV",
            logo = "https://i.imgur.com/mWZPOvl.png",
            url = "http://cdn2.ujjina.com:1935/iptvovacion1/liveovacion1tv/playlist.m3u8",
            category = "Perú"
        ))

        // Colombia
        channels.add(Movie(
            id = "col_trece",
            title = "Canal Trece",
            logo = "https://i.imgur.com/yv9g7fh.png",
            url = "https://stream.logicideas.media/canaltrece-live/smil:live.smil/playlist.m3u8",
             category = "Colombia"
        ))

        // Francia
        channels.add(Movie(
            id = "fr_tv5",
            title = "TV5 Monde Info",
            logo = "https://i.imgur.com/dQs8hQv.png",
            url = "https://ott.tv5monde.com/Content/HLS/Live/channel(info)/variant.m3u8",
            category = "Francia"
        ))

        // Canada
        channels.add(Movie(
            id = "ca_ici",
            title = "ICI Montréal",
            logo = "https://i.imgur.com/Z1b2TJD.png",
            url = "https://amdici.akamaized.net/hls/live/873426/ICI-Live-Stream/master.m3u8",
            category = "Canadá"
        ))

        // USA
        channels.add(Movie(
            id = "usa_abc",
            title = "ABC News Live",
            logo = "https://i.imgur.com/zyYxh6b.png",
            url = "https://content.uplynk.com/channel/3324f2467c414329b3b0cc5cd987b6be.m3u8",
            category = "USA"
        ))
        channels.add(Movie(
            id = "usa_cbs",
            title = "CBS News",
            logo = "https://i.imgur.com/W5UeZB7.png",
            url = "https://cbsn-us.cbsnstream.cbsnews.com/out/v1/55a8648e8f134e82a470f83d562deeca/master.m3u8",
            category = "USA"
        ))
        channels.add(Movie(
            id = "usa_fox",
            title = "Fox News Now",
            logo = "https://i.imgur.com/Kx9X4Y8.png",
            url = "https://fox-foxnewsnow-samsungus.amagi.tv/playlist.m3u8",
            category = "USA"
        ))
        channels.add(Movie(
            id = "usa_pbs",
            title = "PBS Kids",
            logo = "https://i.imgur.com/8CvIJLI.png",
            url = "https://2-fss-2.streamhoster.com/pl_140/amlst:200914-1298290/playlist.m3u8",
            category = "USA"
        ))

        // Mexico
        channels.add(Movie(
            id = "mx_nu9ve",
            title = "NU9VE",
            logo = "https://i.imgur.com/9Cd4NRx.png",
            url = "https://channel04-notusa.akamaized.net/hls/live/2029754-b/event01/index.m3u8",
            category = "México"
        ))
        channels.add(Movie(
            id = "mx_once",
            title = "Canal Once",
            logo = "https://i.imgur.com/bvPmOvB.png",
            url = "https://vivo.canaloncelive.tv/alivepkgr3/ngrp:cepro_all/playlist.m3u8",
            category = "México"
        ))
         channels.add(Movie(
            id = "mx_multi",
            title = "Multimedios Monterrey",
            logo = "https://i.imgur.com/Gv20F9e.png",
            url = "https://mdstrm.com/live-stream-playlist/57b4dbf5dbbfc8f16bb63ce1.m3u8",
            category = "México"
        ))
        channels.add(Movie(
            id = "mx_c6",
            title = "Canal 6 Monterrey",
            logo = "https://i.imgur.com/sY4N8Ru.png",
            url = "https://60417ddeaf0d9.streamlock.net/telemetrika3/telemetrika3.smil/playlist.m3u8",
            category = "México"
        ))
        channels.add(Movie(
            id = "mx_8tv",
            title = "8TV Guadalajara",
            logo = "https://i.imgur.com/VdBGkdl.png",
            url = "https://60417ddeaf0d9.streamlock.net/telemetrika/telemetrika.smil/playlist.m3u8",
            category = "México"
        ))
        channels.add(Movie(
            id = "mx_judicial",
            title = "Canal Judicial",
            logo = "https://i.imgur.com/8dkPiV3.png",
            url = "https://live-scjn.ovp-vivaro.digital/ovp-origin-abr/ngrp:6359ef999f3fb_all/playlist.m3u8",
            category = "México"
        ))

        return channels
    }
}
