#Sum of all overdue payments
SELECT SUM(amount) AS TotalOverduePayments
FROM client_record c JOIN client_payment_premiums cp
ON c.member_id = cp.member_ID
WHERE premium_payment_status = "Overdue";

#Sum of all amounts that are complete
SELECT SUM(amount)
FROM client_record c JOIN client_payment_premiums cp
ON c.member_id = cp.member_ID
WHERE premium_payment_status = "Complete";

#Sum of all payout amounts that are complete
SELECT SUM(payout_amount)
FROM payout_of_claims
WHERE payout_status = "Complete";

#Get number of complete claims per health provider
SELECT hr.hospital_name, COUNT(claim_ID) AS Claims
FROM claim_record cr
JOIN hospital_record hr ON cr.hospital_ID = hr.hospital_ID
WHERE claim_status = "Complete"
GROUP BY(hr.hospital_ID)
ORDER BY Claims DESC;

#Get number of complete claims per policy
SELECT cpr.plan_name, COUNT(claim_ID) AS Claims
FROM claim_record cr
JOIN client_policy_record cp
ON cr.member_ID = cp.member_ID
JOIN company_policy_record cpr
ON cpr.plan_ID = cp.plan_ID
WHERE claim_status = "Complete"
GROUP BY(policy_ID)
ORDER BY Claims DESC;

#Get number of claims per illness
SELECT ir.illness_name, COUNT(claim_ID) AS Claims
FROM claim_record cr JOIN illness_record ir
ON cr.illness_ID = ir.illness_ID
WHERE claim_status = "Complete"
GROUP BY ir.illness_ID
ORDER BY Claims DESC;

#Get Total Service Amount Per Illness
SELECT ir.illness_name, SUM(service_amount) AS Total_Service_Amount
FROM claim_record cr JOIN illness_record ir
ON cr.illness_ID = ir.illness_ID
WHERE claim_status = "Complete"
GROUP BY(ir.illness_ID)
ORDER BY Total_Service_Amount DESC;

#Get Total Covered Amount Per Illness
SELECT ir.illness_name, SUM(covered_amount) AS Total_Covered_Amount
FROM claim_record cr JOIN illness_record ir
ON cr.illness_ID = ir.illness_ID
WHERE claim_status = "Complete"
GROUP BY(ir.illness_ID)
ORDER BY Total_Covered_Amount DESC;