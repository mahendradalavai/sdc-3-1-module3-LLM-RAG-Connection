package com.example.data.local

object PreloadedData {
    val sampleDocuments = listOf(
        DocumentEntity(
            id = 1,
            title = "QuantumTech Apex-X (QX-900) Operations Manual",
            category = "Hardware Spec",
            content = """
                QuantumTech Apex-X Server (Model QX-900) Operations and SLA Manual.
                
                1. System Operating Conditions:
                The QX-900 quantum compute module utilizes CryoCore-4 active cooling running at an optimal operating temperature of exactly 1.4 Kelvin. Any thermal excursion above 2.1 Kelvin initiates an automated protective shutdown of the qubit register array.
                
                2. Coolant Maintenance & Replenishment:
                Liquid Helium-3 replenishment must be performed on a strict 90-day cycle by certified technicians holding Level-4 Cryogenic Clearance. The coolant reservoir capacity is 45.5 liters. Failure to log replenishment logs in the QuantumTech Portal within 5 days of the deadline suspends remote diagnostics.
                
                3. Warranty and Error Codes:
                - Error Code ERR-804 indicates Sub-Kelvin Thermal Drift caused by vacuum seal degradation.
                - Under SLA Clause 14.b, an emergency hardware replacement chassis is dispatched within 4 hours if filed directly via the Q-Direct customer portal.
                - Water exposure clause: The use of water-based sprinkler systems in the server room voids all warranty coverage immediately. Only Halon-5 gas fire extinguishing systems are authorized.
                
                4. Power Specifications:
                Requires 480V 3-phase AC input at 60Hz. Maximum transient draw during compressor cycle is 18.2 kW.
            """.trimIndent(),
            isActive = true
        ),
        DocumentEntity(
            id = 2,
            title = "NovaCorp Enterprise Remote Work & Travel Policy (FY26)",
            category = "HR Policy",
            content = """
                NovaCorp Enterprise Remote Work, Travel & Expense Policy for Fiscal Year 2026.
                
                1. Travel Per-Diem Rates:
                Daily meal and incidental per-diem allowance is capped at $85 USD per day for Tier-1 metropolitan areas (San Francisco, New York City, London, Tokyo, Zurich). For all other locations (Tier-2), the daily per-diem is capped at $65 USD. Itemized receipts are required for any single expense exceeding $25 USD.
                
                2. Flight Booking Timelines:
                All business flights must be booked through the enterprise Concur portal. Domestic flight requests require booking at least 14 days in advance of departure. International travel requests require booking at least 28 days in advance and must be approved by the department VP.
                
                3. Home Office Equipment Stipend:
                Eligible full-time remote employees are entitled to a $1,200 USD home office equipment stipend once every 3 calendar years. Expenses must be submitted under General Ledger code EXP-9421 within 60 days of purchase. Any expense claim submitted prior to the 3-year lockout period expiration is automatically rejected by the automated payroll auditing system.
                
                4. International Remote Work:
                Employees may work remotely from outside their home country for up to a maximum of 20 business days per fiscal year. A written travel authorization request must be approved by both Corporate Security and People Operations at least 30 days prior to departure.
            """.trimIndent(),
            isActive = true
        ),
        DocumentEntity(
            id = 3,
            title = "AeroDrone Vanguard-7 Operational Guide & Regulations",
            category = "Flight Manual",
            content = """
                AeroDrone Vanguard-7 Autonomous Aerial Surveillance System Operator Manual.
                
                1. Flight Boundaries & Weather Limits:
                Maximum legal operational altitude is 120 meters Above Ground Level (AGL) in compliance with CAD-88 civil aviation safety directives. The aircraft is certified for sustained operations in wind speeds up to 38 km/h. Missions must be immediately aborted and Return-To-Home (RTH) initiated if sustained wind gusts exceed 42 km/h.
                
                2. Emergency Beacon & Telemetry Loss:
                If telemetry connection between the ground station and drone is interrupted for longer than 15 seconds, the drone enters FailSafe-Alpha, immediately deploying the ballistic recovery parachute. Concurrently, the automated emergency locator beacon activates on frequency 406.025 MHz.
                
                3. Battery Decommissioning Protocol:
                The Vanguard-7 operates on dual PX-700 LiPo high-discharge cells. Battery packs must be retired and decommissioned from flight duty upon reaching 250 recharge cycles or if internal cell impedance exceeds 18 milliohms measured at 20°C. Never attempt fast-charging below 5°C ambient temperature.
            """.trimIndent(),
            isActive = true
        )
    )
}
