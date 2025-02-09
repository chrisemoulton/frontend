package test

import com.gu.facia.api.models.CollectionConfig
import com.gu.facia.api.models.FaciaContent
import common.editions.{Au, Uk, Us}
import common.{AkkaAgent, Edition}
import controllers.front.Front
import model.{PressedPage, _}
import model.facia.PressedCollection
import org.joda.time.DateTime
import services.FaciaContentConvert
import com.gu.contentapi.client.model.{Content => ApiContent}

object TestContent {
  def newFaciaContent(u: String): FaciaContent = {
    val content = ApiContent(
      id = u,
      sectionId = None,
      sectionName = None,
      webPublicationDateOption = Some(DateTime.now),
      webTitle = "",
      webUrl = "",
      apiUrl = "",
      elements = None,
      fields = Some(Map("liveBloggingNow" -> "true"))
    )
    FaciaContentConvert.contentToFaciaContent(content)
  }
}

class TestPageFront(val id: String, edition: Edition, faciaPage: PressedPage) {
  val query = null
  def close() = {}
  def apply(): Option[PressedPage] = Some(faciaPage)
}

trait ModelHelper {
  val emptyConfig = CollectionConfig.empty
}

trait FaciaTestData extends ModelHelper {

  val ukFrontTrailIds: Seq[String] =
    Seq(
      "/education/2013/oct/08/england-young-people-league-table-basic-skills-oecd",
      "/society/2013/oct/08/malaria-vaccine-trial-children-babies",
      "/world/2013/oct/08/brazil-accuses-canada-spying-nsa-leaks",
      "/film/2013/oct/08/gravity-science-astrophysicist",
      "/money/work-blog/2013/oct/08/long-hours-culture-overworked"
    )

  val usFrontTrailIds: Seq[String] =
    Seq(
      "/world/2013/oct/07/obama-al-liby-capture-legal-system",
      "/world/2013/oct/07/obama-john-boehner-clean-budget-bill",
      "/world/2013/oct/08/palestinian-territories-israel-control-hurting-economy",
      "/commentisfree/2013/oct/07/government-shutdown-how-it-ends",
      "/commentisfree/2013/oct/07/miley-cyrus-music-business-women-sinead-oconnor"
    )

  val auFrontTrailIds: Seq[String] =
    Seq(
      "/world/2013/oct/08/abbott-defends-travel-allowance-claims",
      "/technology/2013/oct/07/australias-fastmail-secure-email-nsa",
      "/world/2013/oct/08/abbott-apologises-asylum-malaysia-solution",
      "/world/2013/oct/07/no-threats-west-papuans-consulate",
      "/commentisfree/2013/oct/07/feminism-rebranding-man-hater"
    )

  val cultureTrailIds: Seq[String] =
    Seq(
      "/film/2013/oct/08/gravity-science-astrophysicist",
      "/music/2013/oct/08/annie-lennox-pornographic-miley-cyrus",
      "/film/2013/oct/08/oscars-best-foreign-language-rules-revised",
      "/music/2013/oct/08/lady-gaga-artpop-album-cover",
      "/technology/gamesblog/2013/oct/03/red-cross-players-accountable-war-crimes"
    )


  val ukFrontTrails: Seq[FaciaContent]= ukFrontTrailIds map TestContent.newFaciaContent
  val usFrontTrails: Seq[FaciaContent]= usFrontTrailIds map TestContent.newFaciaContent
  val auFrontTrails: Seq[FaciaContent]= auFrontTrailIds map TestContent.newFaciaContent

  val cultureFrontTrails: Seq[FaciaContent] = cultureTrailIds map TestContent.newFaciaContent

  val ukFaciaPage: PressedPage = PressedPage(
    id = "uk",
    SeoData.fromPath("uk"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "uk/news/regular-stories",
        displayName = "",
        curated = ukFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
      )
    )

  val usFaciaPage: PressedPage = PressedPage(
    id = "us",
    SeoData.fromPath("us"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "us/news/regular-stories",
        displayName = "",
        curated = usFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
    )
  )

  val auFaciaPage: PressedPage = PressedPage(
    id = "us",
    SeoData.fromPath("us"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "au/news/regular-stories",
        displayName = "",
        curated = auFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
    )
  )

  val ukCultureFaciaPage: PressedPage = PressedPage(
    id = "uk/culture",
    SeoData.fromPath("uk/culture"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "uk/culture/regular-stories",
        displayName = "",
        curated = cultureFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
    )
  )

  val usCultureFaciaPage: PressedPage = PressedPage(
    id = "us/culture",
    SeoData.fromPath("us/culture"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "au/culture/regular-stories",
        displayName = "",
        curated = cultureFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
    )
  )

  val auCultureFaciaPage: PressedPage = PressedPage(
    id = "au/culture",
    SeoData.fromPath("au/culture"),
    FrontProperties.empty,
    collections = List(
      PressedCollection(
        id = "au/culture/regular-stories",
        displayName = "",
        curated = cultureFrontTrails.toList,
        backfill = Nil,
        treats = Nil,
        lastUpdated = None,
        updatedBy = None,
        updatedEmail = None,
        href = None,
        description = None,
        apiQuery = None,
        collectionType = "",
        groups = None,
        uneditable = false,
        showTags = false,
        showSections = false,
        hideKickers = false,
        showDateHeader = false,
        showLatestUpdate = false,
        config = CollectionConfig.empty)
    )
  )

  val defaultAgentContents: Map[String, TestPageFront] = Map(
    ("uk", new TestPageFront("uk", Uk, ukFaciaPage)),
    ("us", new TestPageFront("us", Us, usFaciaPage)),
    ("au", new TestPageFront("au", Au, auFaciaPage)),
    ("uk/culture", new TestPageFront("uk/culture", Uk, ukCultureFaciaPage)),
    ("us/culture", new TestPageFront("us/culture", Us, usCultureFaciaPage)),
    ("au/culture", new TestPageFront("au/culture", Au, auCultureFaciaPage))
  )
}

class TestFront extends Front with FaciaTestData {
  val pageFrontAgent = AkkaAgent[Map[String, TestPageFront]](defaultAgentContents)
}
