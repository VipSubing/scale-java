function scoreCalculator(data) {
  var questions = JSON.parse(data);

  var totalScore = 0;
  var totalPositiveScore = 0;
  var positiveItemCount = 0;
  var factorScores = {
    somatization: 0,
    obsessiveCompulsive: 0,
    interpersonalSensitivity: 0,
    depression: 0,
    anxiety: 0,
    hostility: 0,
    phobicAnxiety: 0,
    paranoidIdeation: 0,
    psychoticism: 0,
    additionalItems: 0,
  };

  var factorItems = {
    somatization: [1, 4, 12, 27, 40, 42, 48, 49, 52, 53, 56, 58],
    obsessiveCompulsive: [3, 9, 10, 28, 38, 45, 46, 51, 55, 65],
    interpersonalSensitivity: [6, 21, 34, 36, 37, 41, 61, 69, 73],
    depression: [5, 14, 15, 20, 22, 26, 29, 30, 31, 32, 54, 71, 79],
    anxiety: [2, 17, 23, 33, 39, 57, 72, 78, 80, 86],
    hostility: [11, 24, 63, 67, 74, 81],
    phobicAnxiety: [13, 25, 47, 50, 70, 75, 82],
    paranoidIdeation: [8, 18, 43, 68, 76, 83],
    psychoticism: [7, 16, 35, 62, 77, 84, 85, 87, 88, 90],
    additionalItems: [19, 44, 59, 60, 64, 66, 89],
  };

  questions.forEach(function (question, index) {
    question.answers.forEach(function (answer) {
      if (answer.selected) {
        totalScore += answer.score;
        if (answer.score >= 2) {
          positiveItemCount++;
          totalPositiveScore += answer.score;
        }

        // Assign scores to factors
        for (var factor in factorItems) {
          if (factorItems[factor].indexOf(index + 1) !== -1) {
            factorScores[factor] += answer.score;
          }
        }
      }
    });
  });

  var totalSymptomIndex = Number((totalScore / questions.length).toFixed(2));
  var positiveSymptomDistressIndex = Number(
    (totalPositiveScore / positiveItemCount).toFixed(2)
  );

  return JSON.stringify({
    totalScore: totalScore,
    totalSymptomIndex: totalSymptomIndex,
    positiveItemCount: positiveItemCount,
    positiveSymptomDistressIndex: positiveSymptomDistressIndex,
    factorScores: factorScores,
  });
}
