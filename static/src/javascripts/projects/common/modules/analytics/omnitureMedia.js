/* global guardian, s */
define([
    'qwery',
    'common/utils/config',
    'common/modules/analytics/omniture',
    'lodash/objects/values',
    'common/utils/chain',
    'common/modules/experiments/ab'
], function (qwery, config, omniture, values, chain, ab) {

    function OmnitureMedia(player) {

        function getAttribute(attributeName) {
            return player.el().getAttribute(attributeName);
        }

        var lastDurationEvent, durationEventTimer,
            mediaId = getAttribute('data-embed-path') || config.page.pageId,
            // infer type (audio/video) from what element we have
            mediaType = qwery('audio', player.el()).length ? 'audio' : 'video',
            contentStarted = false,
            prerollPlayed = false,
            isEmbed = !!guardian.isEmbed,
            events = {
                // this is the expected ordering of events
                'video:request': 'event98',
                'preroll:request': 'event97',
                'preroll:play': 'event59',
                'preroll:skip': 'event99',
                'preroll:end': 'event64',
                'video:play': 'event17',
                'audio:play': 'event19',
                'video:25': 'event21',
                'video:50': 'event22',
                'video:75': 'event23',
                'video:end': 'event18',
                'audio:end': 'event20',
                'video:fullscreen': 'event96',
                // extra events with no set ordering
                duration: 'event57'
            },
            abTestParticipation = ab.makeOmnitureTag(),
            trackingVars = [
                // these tracking vars are specific to media events.
                'eVar11',   // embedded or on platform
                'prop41',   // preroll milestone
                'prop43',   // media type
                'prop44',   // media id
                'eVar44',   // media id
                'eVar74',   // ad or content
                'eVar61'];  // restricted

        this.getDuration = function () {
            return parseInt(getAttribute('data-duration'), 10) || undefined;
        };

        this.getPosition = function () {
            return player.currentTime();
        };

        this.play = function () {
            if (mediaType === 'video' && contentStarted) {
                this.startDurationEventTimer();
            }
        };

        this.pause = function () {
            if (mediaType === 'video') {
                this.stopDurationEventTimer();
            }
        };

        this.sendEvent = function (event, eventName, ad) {
            s.eVar74 = ad ?  mediaType + ' ad' : mediaType + ' content';

            // Set these each time because they are shared global variables, but OmnitureMedia is instanced.
            s.eVar43 = s.prop43 = mediaType.charAt(0).toUpperCase() + mediaType.slice(1);
            s.eVar44 = s.prop44 = mediaId;
            if (prerollPlayed) {
                // Any event after 'video:preroll:play' should be tagged with this value.
                s.prop41 = 'PrerollMilestone';
            }
            s.linkTrackVars = omniture.getStandardProps() + ',' + chain(trackingVars).join(',');
            s.linkTrackEvents = values(events).join(',');
            s.events = event;
            s.tl(true, 'o', eventName || event);
            s.prop41 = s.eVar44 = s.prop44 = s.eVar43 = s.prop43 = undefined;

            s.list1 = abTestParticipation;

        };

        this.sendNamedEvent = function (eventName, ad) {
            this.sendEvent(events[eventName], eventName, ad);
        };

        this.omnitureInit = function () {
            s.loadModule('Media');
            s.Media.autoTrack = false;
            s.Media.trackWhilePlaying = false;
            s.Media.trackVars = omniture.getStandardProps() + ',' + chain(trackingVars).join(',');
            s.Media.trackEvents = values(events).join(',');
            s.Media.segmentByMilestones = false;
            s.Media.trackUsingContextData = false;

            s.eVar11 = isEmbed ? 'Embedded' : config.page.sectionName || '';
            s.eVar7 = s.pageName;

            s.list1 = abTestParticipation;

            s.Media.open(mediaId, this.getDuration(), 'HTML5 Video');

            if (mediaType === 'video') {
                this.sendNamedEvent('video:request');
            }
        };

        this.getDurationWatched = function () { // get the duration watched since this function was last called
            var durationWatched = 0,
                now = new Date(),
                delta = (now - lastDurationEvent) / 1000.0;
            if (durationEventTimer && contentStarted && delta > 1) {
                durationWatched = Math.round(delta);
            }
            lastDurationEvent = now;
            return durationWatched;
        };

        this.baseDurationEvent = function () {
            var evts = [],
                durationWatched = this.getDurationWatched();
            if (durationWatched) {
                evts.push(events.duration + '=' + durationWatched);
            }
            return evts;
        };

        this.sendSegment = function (segment) {
            var evts = this.baseDurationEvent();
            evts.push(segment); // custom quartile completed event
            this.sendEvent(evts.join(','));
        };

        this.sendDurationEvent = function () {
            var evts = this.baseDurationEvent();
            if (evts && evts.length > 0) {
                this.sendEvent(evts.join(','));
            }
        };

        this.startDurationEventTimer = function () {
            this.stopDurationEventTimer();
            lastDurationEvent = new Date();
            durationEventTimer = window.setInterval(this.sendDurationEvent.bind(this), 10000);
        };

        this.stopDurationEventTimer = function () {
            this.sendDurationEvent(); // send any partial duration before stopping
            if (durationEventTimer) {
                window.clearInterval(durationEventTimer);
            }
            durationEventTimer = false;
        };

        this.onContentPlay = function () {
            contentStarted = true;
            this.sendNamedEvent('video:play');
            this.startDurationEventTimer();
        };

        this.onPrerollPlay = function () {
            prerollPlayed = true;
            this.sendNamedEvent('preroll:play', true);
        };

        this.init = function () {

            this.omnitureInit();

            player.on('play', this.play.bind(this));
            player.on('pause', this.pause.bind(this));

            player.one('video:preroll:request', this.sendNamedEvent.bind(this, 'preroll:request', true));
            player.one('video:preroll:play', this.onPrerollPlay.bind(this));
            player.one('video:preroll:skip', this.sendNamedEvent.bind(this, 'preroll:skip', true));
            player.one('video:preroll:end', this.sendNamedEvent.bind(this, 'preroll:end', true));
            player.one('video:content:play', this.onContentPlay.bind(this));
            player.one('audio:content:play', this.sendNamedEvent.bind(this, 'audio:play'));

            player.one('video:content:25', this.sendNamedEvent.bind(this, 'video:25'));
            player.one('video:content:50', this.sendNamedEvent.bind(this, 'video:50'));
            player.one('video:content:75', this.sendNamedEvent.bind(this, 'video:75'));
            player.one('video:content:end', this.sendNamedEvent.bind(this, 'video:end'));
            player.one('audio:content:end', this.sendNamedEvent.bind(this, 'audio:end'));
            player.on('player:fullscreen', this.sendNamedEvent.bind(this, 'video:fullscreen'));
        };
    }
    return OmnitureMedia;
});
