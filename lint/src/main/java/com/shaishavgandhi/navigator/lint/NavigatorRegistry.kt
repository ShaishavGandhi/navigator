package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService

@AutoService(NavigatorRegistry::class)
class NavigatorRegistry(override val issues: List<Issue> = listOf(NavigatorBindDetector.ISSUE)) : IssueRegistry() {
  override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
  override val minApi: Int = 2
}
