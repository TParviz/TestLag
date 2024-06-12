package com.example.testlag.ui

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testlag.R
import com.example.testlag.databinding.FragmentGraphicBinding
import com.example.testlag.ui.adapter.PointTableAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GraphicFragment : Fragment() {

    private var _binding: FragmentGraphicBinding? = null
    private val binding get() = _binding!!

    private val args: GraphicFragmentArgs by navArgs()
    private val viewModel: HomeViewModel by viewModels()

    private val tableAdapter = PointTableAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Получение точек на основе аргумента count
        viewModel.getPoints(args.count.toInt())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initView() = with(binding) {
        // Установка текста "Нет данных" для графика
        graph.setNoDataText("")
        rvPoints.adapter = tableAdapter
    }

    // Инициализация наблюдателей для LiveData и Flow
    private fun initObservers() = with(viewModel) {
        // Наблюдатель за списком точек
        pointList.observe(viewLifecycleOwner) { list ->
            // Обновление данных в адаптере
            tableAdapter.submitList(list)
            // Установка данных для графика
            setGraph(list.map { Entry(it.x, it.y) })
        }

        // Наблюдатель за состоянием загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    isLoading.collect {
                        // Показать или скрыть индикатор загрузки
                        binding.progressBar.isVisible = it
                    }
                }
            }
        }
        // Наблюдатель за ошибками
        error.observe(viewLifecycleOwner) {
            if (it)
            // Возврат на предыдущий экран при ошибке
                findNavController().popBackStack()
        }
    }

    // Метод для установки данных на график
    private fun setGraph(values: List<Entry>) {
        // Включение возможности касания и масштабирования графика
        binding.graph.setTouchEnabled(true)
        binding.graph.setPinchZoom(true)

        // Создание и настройка LineDataSet
        val lineDataSet: LineDataSet
        if (binding.graph.data != null && binding.graph.data.dataSetCount > 0) {
            lineDataSet = binding.graph.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = values
            binding.graph.data.notifyDataChanged()
            binding.graph.notifyDataSetChanged()
        } else {
            lineDataSet = LineDataSet(values, getString(R.string.line))
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.enableDashedLine(10f, 5f, 0f)
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f)
            lineDataSet.color = Color.RED
            lineDataSet.setCircleColor(Color.DKGRAY)
            lineDataSet.lineWidth = 2f
            lineDataSet.circleRadius = 4f
            lineDataSet.setDrawCircleHole(false)
            lineDataSet.valueTextSize = 9f
            lineDataSet.setDrawFilled(true)
            lineDataSet.formLineWidth = 1f
            lineDataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            lineDataSet.formSize = 15f

            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet)

            val data = LineData(dataSets)
            binding.graph.data = data
            binding.graph.invalidate()
        }
        // Отображение графика
        binding.graph.isVisible = true
        binding.graph.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}