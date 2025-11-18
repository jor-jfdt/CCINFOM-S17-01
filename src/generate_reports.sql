USE insurance_database;

#Sum of all overdue payments
SELECT SUM(amount) AS TotalOverduePayments
FROM clients c JOIN client_payment cp
ON c.member_id = cp.member_ID
WHERE LOWER(premium_payment_status) = "overdue";

#Sum of all amounts that are complete
SELECT SUM(amount)
FROM clients c JOIN client_payment cp
ON c.member_id = cp.member_ID
WHERE LOWER(premium_payment_status) = "complete";

#Sum of all payout amounts that are complete
SELECT SUM(payout_amount)
FROM payout
WHERE LOWER(payout_status) = "complete";

#Get number of complete claims per health provider
SELECT hr.hospital_name, COUNT(claim_ID) AS Claims
FROM claim cr
JOIN hospital hr ON cr.hospital_ID = hr.hospital_ID
WHERE LOWER(claim_status) = "complete"
GROUP BY(hr.hospital_ID)
ORDER BY Claims DESC;

#Get number of complete claims per policy
SELECT cpr.plan_name, COUNT(claim_ID) AS Claims
FROM claim cr
JOIN client_policy cp
ON cr.member_ID = cp.member_ID
JOIN policy cpr
ON cpr.plan_ID = cp.plan_ID
WHERE LOWER(claim_status) = "complete"
GROUP BY(policy_ID)
ORDER BY Claims DESC;

#Get number of claims per illness
SELECT ir.illness_name, COUNT(claim_ID) AS Claims
FROM claim cr JOIN illness ir
ON cr.illness_ID = ir.illness_ID
WHERE LOWER(claim_status) = "complete"
GROUP BY ir.illness_ID
ORDER BY Claims DESC;

#Get Total Service Amount Per Illness
SELECT ir.illness_name, SUM(service_amount) AS Total_Service_Amount
FROM claim cr JOIN illness ir
ON cr.illness_ID = ir.illness_ID
WHERE LOWER(claim_status) = "complete"
GROUP BY(ir.illness_ID)
ORDER BY Total_Service_Amount DESC;

#Get Total Covered Amount Per Illness
SELECT ir.illness_name, SUM(covered_amount) AS Total_Covered_Amount
FROM claim cr JOIN illness ir
ON cr.illness_ID = ir.illness_ID
WHERE LOWER(claim_status) = "complete"
GROUP BY(ir.illness_ID)
ORDER BY Total_Covered_Amount DESC;