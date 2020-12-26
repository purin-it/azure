package com.example.demo;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;

@Controller
public class DemoController {

	/** DBからユーザーデータを取得するリポジトリ */
	@Autowired
	private UserDataRepository userDataRepository;

	/**
	 * 検索一覧画面を初期表示する
	 * @param model Modelオブジェクト
	 * @return 検索一覧画面
	 */
	@GetMapping("/")
	public String index(Model model) {
		SearchForm searchForm = new SearchForm();
		model.addAttribute("searchForm", searchForm);
		return "list";
	}

	/**
	 * 検索条件に合うユーザーデータを取得し、一覧に表示する
	 * @param searchForm 検索条件Form
	 * @param model      Modelオブジェクト
	 * @return 検索一覧画面
	 */
	@PostMapping("/search")
	public String search(SearchForm searchForm, Model model) {
		// 検索条件に合うユーザーデータを取得する
		ArrayList<UserData> userDataList = search(searchForm);
		searchForm.setUserDataList(userDataList);

		model.addAttribute("searchForm", searchForm);
		return "list";
	}

	/**
	 * DBから検索条件に合うユーザーデータを取得する
	 * @param searchForm 検索条件Form
	 * @return 検索結果
	 */
	private ArrayList<UserData> search(SearchForm searchForm) {
		Flux<UserData> userDataFlux = null;

		// 検索条件に名前、性別が両方指定されている場合
		if (!StringUtils.isEmpty(searchForm.getSearchName()) && !StringUtils.isEmpty(searchForm.getSearchSex())) {
			userDataFlux = userDataRepository.findByNameContainsAndSex(searchForm.getSearchName(),
					searchForm.getSearchSex());
			// 検索条件に名前が指定されている場合
		} else if (!StringUtils.isEmpty(searchForm.getSearchName())) {
			userDataFlux = userDataRepository.findByNameContains(searchForm.getSearchName());
			// 検索条件に性別が指定されている場合
		} else if (!StringUtils.isEmpty(searchForm.getSearchSex())) {
			userDataFlux = userDataRepository.findBySex(searchForm.getSearchSex());
			// 検索条件未指定の場合
		} else {
			userDataFlux = userDataRepository.findAll();
		}

		// 性別(1,2)を(男,女)に変換し返す
		ArrayList<UserData> userDataList = new ArrayList<>();
		for (UserData userData : userDataFlux.collectList().block()) {
			userData.setSex("1".equals(userData.getSex()) ? "男" : "女");
			userDataList.add(userData);
		}
		return userDataList;
	}

}