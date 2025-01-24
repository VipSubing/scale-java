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
  if (totalScore <= 13) {
    level = "低压力水平";
    summary =
      "您的得分为" +
      totalScore +
      "分，表明您目前的压力水平较低，心理状态良好。";
  } else if (totalScore >= 14 && totalScore <= 26) {
    level = "中等压力水平";
    summary =
      "您的得分为" +
      totalScore +
      "分，表明您目前处于中等压力水平，建议适当关注自身压力状况。";
  } else {
    level = "高压力水平";
    summary =
      "您的得分为" +
      totalScore +
      "分，表明您目前的压力水平较高，建议采取适当的减压措施，必要时寻求专业帮助。";
  }
  // 返回结果
  return JSON.stringify({
    score: totalScore,
    level: level,
    summary: summary,
  });
}
