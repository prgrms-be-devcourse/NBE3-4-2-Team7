package com.tripmarket.domain.member.repository

import com.tripmarket.domain.member.entity.Member
import com.tripmarket.domain.member.entity.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): Optional<Member>

    fun findByEmail(email: String): Optional<Member>
}
