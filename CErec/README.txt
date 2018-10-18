TWG Category Recommendation System based on user behavior:
---------------------------------------------------------

Arguements:

1) training ARFF file containing all user data
2) Active user ARFF file containing user we want to get recommendations for



Two files have been provided for testing purposes.
Expected output:

Recommended categories user has not interacted with
---------------------------------------------------
nintendo: 2.276385716715535
dvd&blu-rayplayers: 2.2505138054943545
playstation: 2.2310641245909784
digitalcameras: 2.096162479918343
blasters&nerf: 2.0802070430591173
hoses&watering: 2.030376514242808
documentaries: 2.0044086628985083

Recommended categories user has interacted with
---------------------------------------------------
sleepwearwomens
underwear&sleepwear
gardentools


The code makes use of the WEKA library which provides machine learning algorithms. The algorithm used here is K-Nearest-Neighbour.
A matrix made up of the categories the user has not interacted with that their nearest neighbours did interact with is created.
A weighted score (based on how close the neighbour was to the user) is calculated for each category. The 7 categories that score the
highest and the 3 categories the user interacted with the most are recommended to the user.

