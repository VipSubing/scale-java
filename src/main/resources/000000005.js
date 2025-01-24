function scoreCalculator(str) {
  // 解析JSON字符串为对象
  var questions = JSON.parse(str);

  // 初始化5个维度的分数
  var scores = {
    N: 0, // 神经质
    E: 0, // 外向性
    O: 0, // 开放性
    A: 0, // 宜人性
    C: 0, // 尽责性
  };

  // 题目对应的维度
  var dimensions = {
    1: "N",
    2: "C",
    3: "A",
    4: "O",
    5: "E",
    6: "N",
    7: "C",
    8: "A",
    9: "O",
    10: "E",
    11: "N",
    12: "C",
    13: "A",
    14: "O",
    15: "E",
    16: "N",
    17: "C",
    18: "A",
    19: "O",
    20: "E",
    21: "N",
    22: "C",
    23: "A",
    24: "O",
    25: "E",
    26: "N",
    27: "C",
    28: "A",
    29: "O",
    30: "E",
    31: "N",
    32: "C",
    33: "A",
    34: "O",
    35: "E",
    36: "N",
    37: "C",
    38: "A",
    39: "O",
    40: "E",
  };
  questions.forEach(function (question, index) {
    question.answers.forEach(function (answer) {
      if (answer.selected) {
        var dimension = dimensions[index + 1];
        if (dimension) {
          scores[dimension] += answer.score;
        }
      }
    });
  });

  // 解释结果
  var result = [];
  var interpretations = {
    N: {
      high: "神经质水平较高，容易感到焦虑、紧张和不安。",
      low: "神经质水平较低，情绪稳定，不易受外界影响。",
      mid: "神经质水平中等，情绪状态正常。",
    },
    E: {
      high: "外向性水平较高，善于社交，喜欢与人交往。",
      low: "外向性水平较低，倾向于独处，不太喜欢社交活动。",
      mid: "外向性水平中等，社交能力正常。",
    },
    O: {
      high: "开放性水平较高，富有想象力，喜欢尝试新事物。",
      low: "开放性水平较低，倾向于传统和习惯的事物。",
      mid: "开放性水平中等，对新旧事物都能接受。",
    },
    A: {
      high: "宜人性水平较高，为人友善，乐于助人。",
      low: "宜人性水平较低，较少考虑他人感受。",
      mid: "宜人性水平中等，人际关系正常。",
    },
    C: {
      high: "尽责性水平较高，做事认真负责，有条理。",
      low: "尽责性水平较低，做事比较随意。",
      mid: "尽责性水平中等，工作态度正常。",
    },
  };

  // 为每个维度添加解释
  Object.keys(scores).forEach(function (dim) {
    var score = scores[dim];
    if (score >= 39) {
      result.push(interpretations[dim].high);
    } else if (score <= 26) {
      result.push(interpretations[dim].low);
    } else {
      result.push(interpretations[dim].mid);
    }
  });

  return JSON.stringify({
    scores: scores,
    result: result,
  });
}
