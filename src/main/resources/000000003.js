function scoreCalculator(questionsJson) {
  // 解析JSON字符串为对象
  var questions = JSON.parse(questionsJson);
  var totalScore = 0;
  questions.forEach(function (question, index) {
    question.answers.forEach(function (answer) {
      if (answer.selected) {
        totalScore += answer.score;
      }
    });
  });

  // 确定焦虑级别
  var level = "";
  var summary = "";
  if (totalScore <= 3) {
    level = "无失眠";
    summary = "您的得分为" + totalScore + "分，睡眠状况良好，无失眠症状。";
  } else if (totalScore >= 4 && totalScore <= 6) {
    level = "轻度失眠";
    summary =
      "您的得分为" +
      totalScore +
      "分，提示可能存在轻度失眠，建议关注睡眠质量。";
  } else if (totalScore >= 7 && totalScore <= 10) {
    level = "中度失眠";
    summary =
      "您的得分为" +
      totalScore +
      "分，提示可能存在中度失眠，建议寻求专业帮助。";
  } else {
    level = "重度失眠";
    summary =
      "您的得分为" + totalScore + "分，提示可能存在重度失眠，建议及时就医。";
  }
  // 返回结果
  return JSON.stringify({
    score: totalScore,
    level: level,
    summary: summary,
  });
}
