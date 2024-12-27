function scoreCalculator(questionsJson) {
  // 解析JSON字符串为对象
  var questions = JSON.parse(questionsJson);

  // 计算总粗分：遍历所有问题，找出被选中的答案的分数并求和
  var rawScore = questions.reduce(function (sum, question) {
    var selectedAnswer = question.answers.filter(function (answer) {
      return answer.selected;
    })[0];
    return sum + (selectedAnswer ? selectedAnswer.score : 0);
  }, 0);

  // 计算标准分 (取整数部分)
  var standardScore = Math.floor(rawScore * 1.25);

  // 判断抑郁程度和生成概述
  var depressionLevel;
  var summary;

  if (standardScore < 53) {
    depressionLevel = "正常";
    summary = "您的得分为" + standardScore + "分，处于正常范围内。";
  } else if (standardScore <= 62) {
    depressionLevel = "轻度抑郁";
    summary =
      "您的得分为" +
      standardScore +
      "分，提示可能存在轻度抑郁，建议寻求专业帮助。";
  } else if (standardScore <= 72) {
    depressionLevel = "中度抑郁";
    summary =
      "您的得分为" + standardScore + "分，提示可能存在中度抑郁，建议及时就医。";
  } else {
    depressionLevel = "重度抑郁";
    summary =
      "您的得分为" + standardScore + "分，提示可能存在重度抑郁，请尽快就医。";
  }

  // 返回JSON字符串
  return JSON.stringify({
    score: standardScore,
    level: depressionLevel,
    summary: summary,
  });
}
