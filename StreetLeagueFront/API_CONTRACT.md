# API CONTRACT — Accommodation Request Confirmation Workflow

## ENDPOINTS SUMMARY

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/coach/travel/accommodation-request` | Submit new request | Required |
| GET | `/api/coach/travel/accommodation-requests/my` | Get coach's requests | Required |
| GET | `/api/coach/travel/accommodation-requests/{id}/pdf` | Download coach request PDF | Required |
| GET | `/api/admin/travel/accommodation-requests` | Get all requests | Required |
| PUT | `/api/admin/travel/accommodation-requests/{id}/approve` | Approve request | Required |
| PUT | `/api/admin/travel/accommodation-requests/{id}/reject` | Reject request | Required |
| GET | `/api/admin/travel/accommodation-requests/{id}/pdf` | Download admin request PDF | Required |

---

## ENDPOINT DETAILS

### 1. SUBMIT ACCOMMODATION REQUEST

**Request:**
```
POST /api/coach/travel/accommodation-request
Content-Type: application/json
Authorization: Bearer {token}

{
  "accommodationId": 1,
  "tournamentId": 1,
  "coachId": "7",
  "coachName": "Coach Name",
  "memberIds": [9, 10, 11, 12],
  "totalAmount": 540.0
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "accommodation": {
    "id": 1,
    "type": "HOTEL",
    "formula": "FULL_BOARD",
    "address": "123 Main St, Sousse",
    "numberOfNights": 3,
    "pricePerNight": 45.0,
    "capacity": 50,
    "status": "APPROVED"
  },
  "tournamentId": 1,
  "coachId": "7",
  "coachName": "Coach Name",
  "memberIds": [9, 10, 11, 12],
  "totalAmount": 540.0,
  "status": "PENDING",
  "adminComment": null,
  "createdAt": "2026-03-29T10:30:00",
  "decidedAt": null
}
```

**Error Response (400):**
```json
{
  "error": "Accommodation not found",
  "status": 400
}
```

---

### 2. GET COACH'S REQUESTS

**Request:**
```
GET /api/coach/travel/accommodation-requests/my?coachId=7
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "accommodation": {
      "id": 1,
      "type": "HOTEL",
      "formula": "FULL_BOARD",
      "address": "123 Main St, Sousse",
      "numberOfNights": 3,
      "pricePerNight": 45.0,
      "capacity": 50,
      "status": "APPROVED"
    },
    "tournamentId": 1,
    "coachId": "7",
    "coachName": "Coach Name",
    "memberIds": [9, 10, 11, 12],
    "totalAmount": 540.0,
    "status": "APPROVED",
    "adminComment": "All set for tournament",
    "createdAt": "2026-03-29T10:30:00",
    "decidedAt": "2026-03-29T11:00:00"
  },
  {
    "id": 2,
    "accommodation": { ... },
    "status": "PENDING",
    ...
  }
]
```

**Empty Response (200 OK):**
```json
[]
```

---

### 3. GET ALL ACCOMMODATION REQUESTS (ADMIN)

**Request:**
```
GET /api/admin/travel/accommodation-requests
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "accommodation": {
      "id": 1,
      "type": "HOTEL",
      "formula": "FULL_BOARD",
      "address": "123 Main St, Sousse",
      "numberOfNights": 3,
      "pricePerNight": 45.0,
      "capacity": 50,
      "status": "APPROVED"
    },
    "tournamentId": 1,
    "coachId": "7",
    "coachName": "Youssef Rahmouni",
    "memberIds": [9, 10, 11, 12],
    "totalAmount": 540.0,
    "status": "PENDING",
    "adminComment": null,
    "createdAt": "2026-03-29T10:30:00",
    "decidedAt": null
  }
]
```

---

### 4. APPROVE ACCOMMODATION REQUEST

**Request:**
```
PUT /api/admin/travel/accommodation-requests/1/approve
Content-Type: application/json
Authorization: Bearer {token}

{
  "adminComment": "Booking confirmed for all 4 members"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "accommodation": { ... },
  "tournamentId": 1,
  "coachId": "7",
  "coachName": "Youssef Rahmouni",
  "memberIds": [9, 10, 11, 12],
  "totalAmount": 540.0,
  "status": "APPROVED",
  "adminComment": "Booking confirmed for all 4 members",
  "createdAt": "2026-03-29T10:30:00",
  "decidedAt": "2026-03-29T11:00:00"
}
```

**Validation Error (400):**
```json
{
  "error": "Request not found",
  "status": 400
}
```

---

### 5. REJECT ACCOMMODATION REQUEST

**Request:**
```
PUT /api/admin/travel/accommodation-requests/1/reject
Content-Type: application/json
Authorization: Bearer {token}

{
  "adminComment": "Accommodation capacity insufficient for team size"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "accommodation": { ... },
  "tournamentId": 1,
  "coachId": "7",
  "coachName": "Youssef Rahmouni",
  "memberIds": [9, 10, 11, 12],
  "totalAmount": 540.0,
  "status": "REJECTED",
  "adminComment": "Accommodation capacity insufficient for team size",
  "createdAt": "2026-03-29T10:30:00",
  "decidedAt": "2026-03-29T11:00:00"
}
```

---

### 6. DOWNLOAD PDF (COACH)

**Request:**
```
GET /api/coach/travel/accommodation-requests/1/pdf
Authorization: Bearer {token}
```

**Response (200 OK):**
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename=accommodation-request-1.pdf`
- Body: PDF file (binary)

**PDF Content (if APPROVED):**
```
═══════════════════════════════════════════════════════════════
                 ACCOMMODATION REQUEST CONFIRMED
                           StreetLeague
═══════════════════════════════════════════════════════════════

Date: 29/03/2026 11:00

REQUEST DETAILS:
  Request ID:  #REQ-1
  Coach:       Youssef Rahmouni
  Status:      APPROVED ✓

ACCOMMODATION DETAILS:
  Type:        HOTEL
  Formula:     Full Board
  Address:     123 Main St, Sousse
  Duration:    3 nights
  Price/Night: 45 TND

SELECTED MEMBERS (4 players)
  ✓ Yassine Bounou
  ✓ Hakim Ziyech
  ✓ Achraf Hakimi
  ✓ Sofiane Feghouli

FINANCIAL SUMMARY:
  Price/Night:      45 TND
  # Nights:         3
  # Members:        4
  TOTAL AMOUNT:     540 TND

───────────────────────────────────────────────────────────────
This document serves as official confirmation of accommodation
reservation for StreetLeague tournament.

Generated on: 29/03/2026 11:00
Admin Comment: Booking confirmed for all 4 members
═══════════════════════════════════════════════════════════════
```

**PDF Content (if REJECTED):**
```
═══════════════════════════════════════════════════════════════
                  ACCOMMODATION REQUEST REJECTED
                           StreetLeague
═══════════════════════════════════════════════════════════════

Date: 29/03/2026 11:00

REQUEST DETAILS:
  Request ID:  #REQ-1
  Coach:       Youssef Rahmouni
  Status:      REJECTED ✗
  Date:        29/03/2026

ACCOMMODATION REQUESTED:
  Type:        HOTEL
  Formula:     Full Board
  Address:     123 Main St, Sousse
  Duration:    3 nights

SELECTED MEMBERS:
  • Yassine Bounou
  • Hakim Ziyech
  • Achraf Hakimi
  • Sofiane Feghouli

REJECTION REASON:
Accommodation capacity insufficient for team size

───────────────────────────────────────────────────────────────
This request has been rejected by StreetLeague administration.
Please contact admin for more information.
Generated on: 29/03/2026 11:00
═══════════════════════════════════════════════════════════════
```

**Error Response (404):**
```
Content-Type: application/json
Status: 404

{
  "error": "Request not found",
  "status": 404
}
```

---

### 7. DOWNLOAD PDF (ADMIN)

**Request:**
```
GET /api/admin/travel/accommodation-requests/1/pdf
Authorization: Bearer {token}
```

**Response:** Same as Coach PDF endpoint

---

## REQUEST/RESPONSE FIELD TYPES

### AccommodationDto
```typescript
{
  id: number;
  type: string;              // "HOTEL", "HOSTEL", "APARTMENT"
  formula: string;           // "FULL_BOARD", "HALF_BOARD", etc.
  address: string;           // "123 Main St, Sousse"
  numberOfNights: number;    // 3
  pricePerNight: number;     // 45.0
  capacity: number;          // 50
  status: string;            // "APPROVED"
}
```

### AccommodationRequestResponseDto
```typescript
{
  id: number;
  accommodation: AccommodationDto;
  tournamentId: number;
  coachId: string;           // Can be email or numeric ID
  coachName: string;
  memberIds: number[];       // [9, 10, 11, 12]
  totalAmount: number;       // 540.0
  status: string;            // "PENDING", "APPROVED", "REJECTED"
  adminComment: string | null;
  createdAt: string;         // ISO 8601: "2026-03-29T10:30:00"
  decidedAt: string | null;  // ISO 8601: "2026-03-29T11:00:00"
}
```

### DecisionDto
```typescript
{
  adminComment: string;      // Optional for approve, required for reject
}
```

---

## HTTP STATUS CODES

| Status | Description |
|--------|-------------|
| 200 | OK — Request successful |
| 201 | Created — Resource created |
| 400 | Bad Request — Invalid input |
| 401 | Unauthorized — Missing/invalid token |
| 403 | Forbidden — Insufficient permissions |
| 404 | Not Found — Resource not found |
| 500 | Internal Server Error |

---

## ERROR RESPONSES

**Format:**
```json
{
  "error": "Error message",
  "status": 400,
  "timestamp": "2026-03-29T10:30:00"
}
```

**Common Errors:**
- `"Accommodation not found"` (400)
- `"Request not found"` (404)
- `"Invalid member list"` (400)
- `"Session expired"` (401)
- `"Unauthorized access"` (403)

---

## AUTHENTICATION

All endpoints require:
```
Authorization: Bearer {token}
```

Token stored in localStorage key: `"TokenUserConnect"`

If token missing → 401 Unauthorized
If token invalid → 401 Unauthorized
If token expired → 401 Unauthorized

---

## NOTES

1. **Member IDs:** Must be valid user IDs from database (role must be PLAYER)
2. **Coach ID:** Can be numeric (7) or email (rbensalem14@gmail.com)
3. **Total Amount:** Calculated frontend but verified backend
4. **PDF Generation:** Uses iText library, generates on-demand (no pre-generation)
5. **Timestamps:** Use ISO 8601 format with timezone info
6. **Status Values:** Only three possible: PENDING, APPROVED, REJECTED
7. **Admin Comment:** Max 500 characters, optional for approve, **required for reject**

---

## TESTING CHECKLIST

- [ ] POST submit returns 200 with ID
- [ ] GET my-requests returns array of requests
- [ ] GET admin-requests returns all requests
- [ ] PUT approve updates status to APPROVED
- [ ] PUT reject updates status to REJECTED
- [ ] GET pdf (approved) returns confirmation PDF
- [ ] GET pdf (rejected) returns rejection PDF
- [ ] Timestamps are ISO 8601 format
- [ ] Admin comments save correctly
- [ ] Member lists are preserved exactly
- [ ] Total amounts match calculations
- [ ] Status badges show correct colors
- [ ] PDF downloads trigger browser download

---

Generated: 2026-03-29
Version: 1.0
