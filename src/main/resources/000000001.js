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

  // 计算标准分
  var standardScore = Math.round(rawScore * 1.25);

  // 确定焦虑级别
  var level = "";
  var summary = "";
  if (standardScore < 50) {
    level = "正常";
    summary = "您的得分为" + standardScore + "分，情绪状况良好，无焦虑倾向。";
  } else if (standardScore >= 50 && standardScore <= 59) {
    level = "轻度焦虑";
    summary =
      "您的得分为" +
      standardScore +
      "分，提示可能存在轻度焦虑，建议关注情绪变化。";
  } else if (standardScore >= 60 && standardScore <= 69) {
    level = "中度焦虑";
    summary =
      "您的得分为" +
      standardScore +
      "分，提示可能存在中度焦虑，建议寻求专业帮助。";
  } else {
    level = "重度焦虑";
    summary =
      "您的得分为" +
      standardScore +
      "分，提示可能存在重度焦虑，建议立即咨询专业人士。";
  }

  // 返回结果
  return JSON.stringify({
    score: standardScore,
    level: level,
    summary: summary,
  });
}
