#### 十个超级实用的git命令

- 查看未合并到master的分支

  git branch --no-merged master

- 列出最近修改过的分支
  
  git for-each-ref --count=30 --sort=-committerdate refs/heads/ --format='%(refname:short)'%
  
- 启用新的vim编写提交信息
  
  git config --global core.editor "vim"
  
- 将未提交的修改丢弃，恢复到之前的干净状态

  git reset --hard
  
- 撤销上一个git提交
  
  git reset HEAD~
  
- 未提交情况下，取消对于某个文件的修改

  git reset HEAD $1 && git checkout $1

- 查看当我们使用git add之后的内容的差异
  
  git diff --cached
  
- 切回上一个分支
  
  git checkout -
  
- 查找包含某个提交的分支列表
  
  git branch --contains  9666b5979(commit hash)
  
- 查找包含某个提交的tag列表

  git tag --contains 9666b5979(commit hash)
  