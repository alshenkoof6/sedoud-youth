package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FollowupStatus
import com.example.data.repository.YouthMeetingRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Youth Meeting", appName)
  }

  @Test
  fun `verify QR attendance recording and streak progression`() {
    val repo = YouthMeetingRepository()
    val activeMeeting = repo.getActiveMeeting()
    assertNotNull(activeMeeting)

    val token = activeMeeting!!.qrToken?.token
    assertNotNull(token)

    val youthId = "YT_000101"
    val initialYouth = repo.youthProfiles.value.first { it.userId == youthId }
    val initialStreak = initialYouth.stats.currentStreak
    val initialPoints = initialYouth.pointsBalance

    val result = repo.recordAttendance(youthId, token!!)
    assertTrue(result.isSuccess)

    val updatedYouth = repo.youthProfiles.value.first { it.userId == youthId }
    assertEquals(initialStreak + 1, updatedYouth.stats.currentStreak)
    assertEquals(initialPoints + activeMeeting.pointsAwarded, updatedYouth.pointsBalance)
    assertEquals(FollowupStatus.GREEN, updatedYouth.stats.followupStatus)
  }

  @Test
  fun `verify duplicate attendance prevention`() {
    val repo = YouthMeetingRepository()
    val activeMeeting = repo.getActiveMeeting()
    val token = activeMeeting!!.qrToken!!.token
    val youthId = "YT_000101"

    val first = repo.recordAttendance(youthId, token)
    assertTrue(first.isSuccess)

    val second = repo.recordAttendance(youthId, token)
    assertFalse(second.isSuccess)
    assertTrue(second.exceptionOrNull()?.message?.contains("مسبقاً") == true)
  }
}

