import tkinter as tk
from tkinter_videoplayer import VideoPlayer

# 1. 创建主窗口
root = tk.Tk()

# 2. 去除标题栏和边框，实现"无边框"
root.overrideredirect(True)

# 3. 获取屏幕尺寸并设置为"全屏"
screen_width = root.winfo_screenwidth()
screen_height = root.winfo_screenheight()
root.geometry(f"{screen_width}x{screen_height}+0+0")

# 4. 实现"窗口置顶"
root.attributes('-topmost', True)

# 5. 创建一个容器框架
container = tk.Frame(root)
container.pack(expand=True, fill="both")

# 6. 创建视频播放器组件
player = VideoPlayer(container, video_path='video.mp4', autoplay=True, controls=False)

# 7. 监听"播放结束"事件，实现"自动关闭"
def on_video_end():
    root.quit()  # 结束主循环
    root.destroy()  # 销毁窗口

player.add_event_listener("ended", on_video_end)

# 启动GUI主循环
root.mainloop()
