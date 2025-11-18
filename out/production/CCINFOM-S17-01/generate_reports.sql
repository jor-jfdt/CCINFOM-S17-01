USE insurance_database;

/*Sum of all overdue payments*/
SELECT COALESCE(SUM(amount), 0) AS total_overdue_payments
FROM clients c JOIN client_payment cp
ON c.member_id = cp.member_id
WHERE LOWER(premium_payment_status) = "overdue"
UNION
/*Sum of all amounts that are complete*/
SELECT COALESCE(SUM(amount), 0) AS total_complete_payments
FROM clients c JOIN client_payment cp
ON c.member_id = cp.member_id
WHERE LOWER(premium_payment_status) = "complete"
UNION
/*Sum of all payout amounts that are complete*/
SELECT COALESCE(SUM(payout_amount), 0) AS total_payouts
FROM payout
WHERE LOWER(payout_status) = "complete";

/*Get number of complete claims per health provider*/
SELECT hr.hospital_name, COUNT(claim_id) AS hospital_claims
FROM claim cr
JOIN hospital hr ON cr.hospital_id = hr.hospital_id
WHERE LOWER(claim_status) = "complete"
GROUP BY hr.hospital_id
ORDER BY hospital_claims DESC;

/*
#Get number of complete claims per policy
SELECT cpr.plan_name, COUNT(claim_id) AS policy_claims
FROM claim cr
JOIN client_policy cp ON cr.member_id = cp.member_id
JOIN policy cpr ON cpr.plan_id = cp.plan_id
WHERE LOWER(claim_status) = "complete"
GROUP BY policy_id
ORDER BY policy_claims DESC;
*/

SELECT
	policy.plan_name,
	COUNT(claim_id) AS policy_claims,
	COALESCE(SUM(service_amount), 0) AS total_service_amount,
	COALESCE(SUM(covered_amount), 0) AS total_covered_amount
FROM claim
JOIN client_policy ON claim.member_id = client_policy.member_id
JOIN policy ON policy.plan_id = client_policy.plan_id
WHERE LOWER(claim_status) = 'complete'
GROUP BY policy.plan_id, policy.plan_name
ORDER BY
	policy_claims DESC,
	total_service_amount DESC,
	total_covered_amount DESC;

/*
#Get number of claims per illness
SELECT ir.illness_name, COUNT(claim_id) AS illness_claims
FROM claim cr JOIN illness ir
ON cr.illness_id = ir.illness_id
WHERE LOWER(claim_status) = "complete"
GROUP BY ir.illness_id
ORDER BY Claims DESC;

#Get Total Service Amount Per Illness
SELECT ir.illness_name, COALESCE(SUM(service_amount), 0) AS total_service_amount
FROM claim cr JOIN illness ir
ON cr.illness_id = ir.illness_id
WHERE LOWER(claim_status) = "complete"
GROUP BY ir.illness_id
ORDER BY total_service_amount DESC;

#Get Total Covered Amount Per Illness
SELECT ir.illness_name, COALESCE(SUM(covered_amount), 0) AS total_covered_amount
FROM claim cr JOIN illness ir
ON cr.illness_id = ir.illness_id
WHERE LOWER(claim_status) = "complete"
GROUP BY ir.illness_id
ORDER BY total_covered_amount DESC;
*/

SELECT
	illness.illness_name,
	COUNT(claim_id) AS illness_claims,
	COALESCE(SUM(service_amount), 0) AS total_service_amount,
	COALESCE(SUM(covered_amount), 0) AS total_covered_amount
FROM claim
JOIN illness ON claim.illness_id = illness.illness_id
WHERE LOWER(claim_status) = 'complete'
GROUP BY illness.illness_id, illness.illness_name
ORDER BY
	illness_claims DESC,
	total_service_amount DESC,
	total_covered_amount DESC;
