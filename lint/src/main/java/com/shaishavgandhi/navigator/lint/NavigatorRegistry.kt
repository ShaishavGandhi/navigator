package com.shaishavgandhi.navigator.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.auto.service.AutoService

@AutoService(IssueRegistry::class)
class NavigatorRegistry(override val issues: List<Issue> = listOf(NavigatorBindDetector.ISSUE)) : IssueRegistry() {
  override val api: Int = CURRENT_API
  override val minApi: Int = 2
}
