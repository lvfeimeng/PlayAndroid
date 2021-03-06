package zqx.rj.com.playandroid

import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_drawer_header.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import zqx.rj.com.mvvm.base.BaseActivity
import zqx.rj.com.mvvm.common.Preference
import zqx.rj.com.mvvm.common.constant.Constant
import zqx.rj.com.mvvm.state.callback.LoginSucListener
import zqx.rj.com.playandroid.account.data.context.LoginContext
import zqx.rj.com.playandroid.account.data.context.LogoutState
import zqx.rj.com.playandroid.account.view.LoginActivity
import zqx.rj.com.playandroid.home.view.fragment.HomeFragment
import zqx.rj.com.playandroid.mine.view.activity.AboutActivity
import zqx.rj.com.playandroid.navigation.view.fragment.NavigationFragment
import zqx.rj.com.playandroid.project.view.fragment.ProjectFragment
import zqx.rj.com.playandroid.system.view.fragment.SystemFragment
import zqx.rj.com.playandroid.wechat.view.fragment.WeChatFragment

class MainActivity : BaseActivity(), LoginSucListener {

    private val HOME = 0
    private val WECHAT = 1
    private val SYSTEM = 2
    private val NAVIGATION = 3
    private val PROJECT = 4

    private lateinit var headView: View
    private val mNotLogin: String = "未登录"
    // 委托属性   将实现委托给了 -> Preference
    private var mUsername: String by Preference(Constant.USERNAME_KEY, mNotLogin)

    private val mHomeFragment by lazy { HomeFragment() }
    private val mWeChatFragment by lazy { WeChatFragment() }
    private val mSystemFragment by lazy { SystemFragment() }
    private val mNavigationFragment by lazy { NavigationFragment() }
    private val mProjectFragment by lazy { ProjectFragment() }

    // 当前显示的 fragment
    private lateinit var mCurrent: Fragment
    private val mFragmentManager by lazy { supportFragmentManager }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {

        initToolBar()
        initDrawerLayout()
        initNavigationBar()
        setDefaultFragment()
    }

    private fun initToolBar() {
        // 设置标题
        setToolBar(toolbar, getString(R.string.app_name))

        //设置导航图标、按钮有旋转特效
        val toggle = ActionBarDrawerToggle(
                this, mDrawerMain, toolbar, R.string.app_name, R.string.app_name)
        mDrawerMain.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initDrawerLayout() {

        // 设置 登录成功 监听
        LoginActivity.listener = this

        // 直接获取报错   error -> mNavMain.mTvName
        headView = mNavMain.getHeaderView(0)
        headView.mTvName.text = mUsername

        // 点击 登录
        headView.mCivIcon.setOnClickListener { LoginContext.instance.login(this) }

        mNavMain.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_menu_collect -> {
                    LoginContext.instance.toCollectActivity(this)
                }
                R.id.nav_menu_about -> {
                    startActivity<AboutActivity>()
                }
                R.id.nav_menu_setting -> {
                    toast(getString(R.string.mine_setting))
                }
                R.id.nav_menu_logout -> {
                    // 清除 cookie、登录缓存
                    Preference.clear()
                    headView.mTvName.text = mNotLogin
                    // 设置 未登录状态
                    LoginContext.instance.mState = LogoutState()
                }
            }

            // 关闭侧边栏
            mDrawerMain.closeDrawers()
            true
        }
    }

    private fun initNavigationBar() {
        with(mNavigationBar) {
            setMode(BottomNavigationBar.MODE_FIXED)

            addItem(BottomNavigationItem(R.mipmap.ic_home, getString(R.string.home)))
            addItem(BottomNavigationItem(R.mipmap.ic_wechat, getString(R.string.wechat)))
            addItem(BottomNavigationItem(R.mipmap.ic_system, getString(R.string.system)))
            addItem(BottomNavigationItem(R.mipmap.ic_navigation, getString(R.string.navigation)))
            addItem(BottomNavigationItem(R.mipmap.ic_project, getString(R.string.project)))

            // 设置底部 BottomBar 默认选中 home
            setFirstSelectedPosition(HOME)
            // 初始化
            initialise()

            setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
                override fun onTabReselected(position: Int) {}
                override fun onTabUnselected(position: Int) {}

                override fun onTabSelected(position: Int) = switchFragment(position)
            })
        }
    }

    // 设置默认选中 fragment
    private fun setDefaultFragment() {
        mCurrent = mHomeFragment
        val transaction = mFragmentManager.beginTransaction()
        transaction.add(R.id.content, mHomeFragment).commit()
    }

    private fun switchFragment(position: Int) {
        when (position) {
            HOME -> {
                setToolBar(toolbar, getString(R.string.home))
                switch(mCurrent, mHomeFragment)
            }
            WECHAT -> {
                setToolBar(toolbar, getString(R.string.wechat))
                switch(mCurrent, mWeChatFragment)
            }

            SYSTEM -> {
                setToolBar(toolbar, getString(R.string.system))
                switch(mCurrent, mSystemFragment)
            }
            NAVIGATION -> {
                setToolBar(toolbar, getString(R.string.navigation))
                switch(mCurrent, mNavigationFragment)
            }
            PROJECT -> {
                setToolBar(toolbar, getString(R.string.project))
                switch(mCurrent, mProjectFragment)
            }
        }
    }

    // 复用 fragment
    private fun switch(from: Fragment, to: Fragment) {
        if (mCurrent != to) {
            mCurrent = to
            val transaction = mFragmentManager.beginTransaction()
            if (to.isAdded)
                transaction.hide(from).show(to)
            else
                transaction.hide(from).add(R.id.content, to)
            transaction.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        //将滑动菜单显示出来
            android.R.id.home -> mDrawerMain.openDrawer(Gravity.START)
        // 跳转到 搜索
            R.id.action_search -> {
                startActivity<SearchActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 登录成功 回调
    override fun success(username: String) {

        // 进行 SharedPreference 存储
        mUsername = username
        headView.mTvName.text = username
    }

}
