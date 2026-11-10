package com.example.gutiverse.engine

/**
 * Configurable rules for the 16 Bead (Sholo Guti) match.
 *
 * Players can customize or toggle these in Settings to test different traditional variants.
 */
data class RulesConfiguration(
    val allowMultiJump: Boolean = true,
    val mandatoryCapture: Boolean = false,
    val turnTimerSeconds: Int = 30,
    val matchTimerSeconds: Int = 300
) {
    companion object {
        val DEFAULT = RulesConfiguration()

        const val DOCUMENTED_RULES_EN = """
1. BOARD & PIECES:
   Played on 37 intersections. Player 1 (Gold) commands 16 beads at the bottom; Player 2 (Cyan / Robot) commands 16 beads at the top. The middle row (5 spots) begins empty.

2. MOVEMENT:
   Beads can move one step along marked lines to any adjacent unoccupied intersection.

3. CAPTURES:
   A bead can jump over an adjacent enemy bead into an immediately following empty point along a straight line. The jumped enemy bead is captured and removed.

4. MULTI-JUMP CHAIN:
   When enabled, if the capturing bead can make another consecutive jump from its landing point, it may continue jumping in the same turn.

5. VICTORY:
   A player wins by capturing all 16 opposing beads, or immobilizing the opponent so they have no legal moves remaining.
"""

        const val DOCUMENTED_RULES_BN = """
১. বোর্ড ও গুটি:
   ৩৭টি মিলনস্থল (পয়েন্ট)। প্রথম খেলোয়াড় (গোল্ড) নিচের ১৬টি গুটি এবং দ্বিতীয় খেলোয়াড় (সায়ান / রোবট) ওপরের ১৬টি গুটি পরিচালনা করেন। মাঝখানের ৫টি পয়েন্ট শুরুতে খালি থাকে।

২. সাধারণ চাল:
   গুটি যেকোনো সংযুক্ত দাগ বরাবর পাশাপাশি ১টি খালি পয়েন্টে এগিয়ে যেতে পারে।

৩. গুটি কাটা (ক্যাপচার):
   প্রতিপক্ষের কোনো গুটির ওপর দিয়ে সোজা লাইনে লাফিয়ে ঠিক পেছনের খালি পয়েন্টে গিয়ে প্রতিপক্ষের গুটিটি কেটে বাদ দেওয়া যায়।

৪. একাধিকবার লাফিয়ে কাটা (Multi-jump):
   একবার গুটি কাটার পর যদি একই গুটি দিয়ে পরপর আরও গুটি কাটার সুযোগ থাকে, তবে এক চালেই তা কাটা যায়।

৫. জয় ও পরাজয়:
   প্রতিপক্ষের সব ১৬টি গুটি কেটে ফেলা অথবা প্রতিপক্ষের সমস্ত গুটি আটকে দিলে জয়ী ঘোষণা করা হয়।
"""
    }
}
