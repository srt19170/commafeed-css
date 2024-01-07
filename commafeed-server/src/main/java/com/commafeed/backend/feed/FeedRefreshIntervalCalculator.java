package com.commafeed.backend.feed;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.commafeed.CommaFeedConfiguration;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class FeedRefreshIntervalCalculator {

	private final boolean heavyLoad;
	private final int refreshIntervalMinutes;

	@Inject
	public FeedRefreshIntervalCalculator(CommaFeedConfiguration config) {
		this.heavyLoad = config.getApplicationSettings().getHeavyLoad();
		this.refreshIntervalMinutes = config.getApplicationSettings().getRefreshIntervalMinutes();
	}

	public Date onFetchSuccess(Date publishedDate, Long averageEntryInterval) {
		Date defaultRefreshInterval = getDefaultRefreshInterval();
		return heavyLoad ? computeRefreshIntervalForHeavyLoad(publishedDate, averageEntryInterval, defaultRefreshInterval)
				: defaultRefreshInterval;
	}

	public Date onFeedNotModified(Date publishedDate, Long averageEntryInterval) {
		Date defaultRefreshInterval = getDefaultRefreshInterval();
		return heavyLoad ? computeRefreshIntervalForHeavyLoad(publishedDate, averageEntryInterval, defaultRefreshInterval)
				: defaultRefreshInterval;
	}

	public Date onFetchError(int errorCount) {
		int retriesBeforeDisable = 3;
		if (errorCount < retriesBeforeDisable || !heavyLoad) {
			return getDefaultRefreshInterval();
		}

		int disabledHours = Math.min(24 * 7, errorCount - retriesBeforeDisable + 1);
		return DateUtils.addHours(new Date(), disabledHours);
	}

	private Date getDefaultRefreshInterval() {
		return DateUtils.addMinutes(new Date(), refreshIntervalMinutes);
	}

	private Date computeRefreshIntervalForHeavyLoad(Date publishedDate, Long averageEntryInterval, Date defaultRefreshInterval) {
		Date now = new Date();

		if (publishedDate == null) {
			// feed with no entries, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		} else if (publishedDate.before(DateUtils.addMonths(now, -1))) {
			// older than a month, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		} else if (publishedDate.before(DateUtils.addDays(now, -14))) {
			// older than two weeks, recheck in 12 hours
			return DateUtils.addHours(now, 12);
		} else if (publishedDate.before(DateUtils.addDays(now, -7))) {
			// older than a week, recheck in 6 hours
			return DateUtils.addHours(now, 6);
		} else if (averageEntryInterval != null) {
			// use average time between entries to decide when to refresh next, divided by factor
			int factor = 2;

			// not more than 6 hours
			long date = Math.min(DateUtils.addHours(now, 6).getTime(), now.getTime() + averageEntryInterval / factor);

			// not less than default refresh interval
			date = Math.max(defaultRefreshInterval.getTime(), date);

			return new Date(date);
		} else {
			// unknown case, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		}
	}

}
