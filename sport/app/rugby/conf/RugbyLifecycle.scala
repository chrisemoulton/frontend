package rugby.conf

import common.{AkkaAsync, ExecutionContexts, Jobs}
import play.api.GlobalSettings
import rugby.feed.OptaFeed
import rugby.jobs.RugbyStatsJob

trait RugbyLifecycle extends GlobalSettings with ExecutionContexts {

  override def onStart(app: play.api.Application) {
    super.onStart(app)
    Jobs.deschedule("MatchDayLiveScores")
    Jobs.schedule("MatchDayLiveScores", "0 * * * * ?") {
      RugbyStatsJob.sendLiveScores(OptaFeed.getLiveScores)
    }

    Jobs.deschedule("FixturesAndResults")
    Jobs.schedule("FixturesAndResults", "0 0/30 * * * ?") {
      RugbyStatsJob.fixturesAndResults(OptaFeed.getFixturesAndResults)
    }

    AkkaAsync {
      RugbyStatsJob.sendLiveScores(OptaFeed.getLiveScores)
      RugbyStatsJob.fixturesAndResults(OptaFeed.getFixturesAndResults)
    }
  }
}